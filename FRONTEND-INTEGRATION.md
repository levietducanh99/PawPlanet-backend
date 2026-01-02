# Frontend Integration Guide - Cloudinary Upload

This guide demonstrates how to integrate the Media Sign API with your frontend application.

## Table of Contents
- [React/TypeScript Example](#reacttypescript-example)
- [Vanilla JavaScript Example](#vanilla-javascript-example)
- [Vue.js Example](#vuejs-example)
- [Error Handling](#error-handling)
- [Best Practices](#best-practices)

---

## React/TypeScript Example

### 1. Define TypeScript Types

```typescript
// types/media.ts
export enum UploadContext {
  USER_AVATAR = 'USER_AVATAR',
  PET_AVATAR = 'PET_AVATAR',
  PET_GALLERY = 'PET_GALLERY',
  POST_MEDIA = 'POST_MEDIA',
  ENCYCLOPEDIA_CLASS = 'ENCYCLOPEDIA_CLASS',
  ENCYCLOPEDIA_SPECIES = 'ENCYCLOPEDIA_SPECIES',
  ENCYCLOPEDIA_BREED = 'ENCYCLOPEDIA_BREED'
}

export interface MediaSignRequest {
  context: UploadContext;
  ownerId?: number;
  slug?: string;
  resourceType?: 'image' | 'video' | 'raw' | 'auto';
}

export interface MediaSignResponse {
  signature: string;
  timestamp: number;
  api_key: string;
  cloud_name: string;
  asset_folder: string;
  public_id?: string;
  resource_type: string;
}

export interface CloudinaryUploadResponse {
  asset_id: string;
  public_id: string;
  version: number;
  version_id: string;
  signature: string;
  width: number;
  height: number;
  format: string;
  resource_type: string;
  created_at: string;
  bytes: number;
  type: string;
  url: string;
  secure_url: string;
}
```

### 2. Create Upload Service

```typescript
// services/cloudinaryService.ts
import { MediaSignRequest, MediaSignResponse, CloudinaryUploadResponse } from '../types/media';

class CloudinaryService {
  private apiBaseUrl = '/api/v1';

  /**
   * Get upload signature from backend
   */
  async getUploadSignature(request: MediaSignRequest): Promise<MediaSignResponse> {
    const response = await fetch(`${this.apiBaseUrl}/media/sign`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${this.getAuthToken()}`
      },
      body: JSON.stringify(request)
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to get upload signature');
    }

    return response.json();
  }

  /**
   * Upload file to Cloudinary
   */
  async uploadFile(
    file: File,
    signData: MediaSignResponse,
    onProgress?: (progress: number) => void
  ): Promise<CloudinaryUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('api_key', signData.api_key);
    formData.append('timestamp', signData.timestamp.toString());
    formData.append('signature', signData.signature);
    formData.append('asset_folder', signData.asset_folder);
    formData.append('resource_type', signData.resource_type);

    if (signData.public_id) {
      formData.append('public_id', signData.public_id);
    }

    const uploadUrl = `https://api.cloudinary.com/v1_1/${signData.cloud_name}/${signData.resource_type}/upload`;

    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest();

      // Track upload progress
      if (onProgress) {
        xhr.upload.addEventListener('progress', (e) => {
          if (e.lengthComputable) {
            const percentComplete = (e.loaded / e.total) * 100;
            onProgress(percentComplete);
          }
        });
      }

      xhr.addEventListener('load', () => {
        if (xhr.status === 200) {
          resolve(JSON.parse(xhr.responseText));
        } else {
          reject(new Error(`Upload failed: ${xhr.statusText}`));
        }
      });

      xhr.addEventListener('error', () => {
        reject(new Error('Upload failed'));
      });

      xhr.open('POST', uploadUrl);
      xhr.send(formData);
    });
  }

  /**
   * Complete upload flow: get signature + upload file
   */
  async uploadWithSignature(
    file: File,
    request: MediaSignRequest,
    onProgress?: (progress: number) => void
  ): Promise<CloudinaryUploadResponse> {
    const signData = await this.getUploadSignature(request);
    return this.uploadFile(file, signData, onProgress);
  }

  private getAuthToken(): string {
    // Get JWT token from your auth system
    return localStorage.getItem('authToken') || '';
  }
}

export const cloudinaryService = new CloudinaryService();
```

### 3. Create Upload Component

```tsx
// components/UserAvatarUpload.tsx
import React, { useState } from 'react';
import { cloudinaryService } from '../services/cloudinaryService';
import { UploadContext } from '../types/media';

interface Props {
  userId: number;
  currentAvatarUrl?: string;
  onUploadSuccess: (url: string) => void;
}

export const UserAvatarUpload: React.FC<Props> = ({
  userId,
  currentAvatarUrl,
  onUploadSuccess
}) => {
  const [uploading, setUploading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | undefined>(currentAvatarUrl);

  const handleFileSelect = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith('image/')) {
      setError('Please select an image file');
      return;
    }

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      setError('File size must be less than 5MB');
      return;
    }

    try {
      setUploading(true);
      setError(null);
      setProgress(0);

      // Create preview
      const objectUrl = URL.createObjectURL(file);
      setPreviewUrl(objectUrl);

      // Upload to Cloudinary
      const result = await cloudinaryService.uploadWithSignature(
        file,
        {
          context: UploadContext.USER_AVATAR,
          ownerId: userId
        },
        setProgress
      );

      // Clean up preview
      URL.revokeObjectURL(objectUrl);

      // Update avatar in your backend
      await updateUserAvatar(result.secure_url);

      // Notify parent component
      onUploadSuccess(result.secure_url);
      setPreviewUrl(result.secure_url);

    } catch (err) {
      setError(err instanceof Error ? err.message : 'Upload failed');
      setPreviewUrl(currentAvatarUrl);
    } finally {
      setUploading(false);
      setProgress(0);
    }
  };

  const updateUserAvatar = async (url: string) => {
    const response = await fetch('/api/v1/users/me/avatar', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('authToken')}`
      },
      body: JSON.stringify({ avatarUrl: url })
    });

    if (!response.ok) {
      throw new Error('Failed to update avatar');
    }
  };

  return (
    <div className="avatar-upload">
      <div className="avatar-preview">
        {previewUrl ? (
          <img src={previewUrl} alt="Avatar" />
        ) : (
          <div className="placeholder">No avatar</div>
        )}
      </div>

      <div className="upload-controls">
        <input
          type="file"
          accept="image/*"
          onChange={handleFileSelect}
          disabled={uploading}
          id="avatar-input"
        />
        <label htmlFor="avatar-input" className="upload-button">
          {uploading ? 'Uploading...' : 'Choose Photo'}
        </label>
      </div>

      {uploading && (
        <div className="progress-bar">
          <div className="progress-fill" style={{ width: `${progress}%` }} />
          <span>{Math.round(progress)}%</span>
        </div>
      )}

      {error && (
        <div className="error-message">{error}</div>
      )}
    </div>
  );
};
```

### 4. Pet Gallery Upload Component

```tsx
// components/PetGalleryUpload.tsx
import React, { useState } from 'react';
import { cloudinaryService } from '../services/cloudinaryService';
import { UploadContext } from '../types/media';

interface Props {
  petId: number;
  onUploadSuccess: (urls: string[]) => void;
}

export const PetGalleryUpload: React.FC<Props> = ({ petId, onUploadSuccess }) => {
  const [uploading, setUploading] = useState(false);
  const [uploadedUrls, setUploadedUrls] = useState<string[]>([]);

  const handleMultipleFiles = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(event.target.files || []);
    if (files.length === 0) return;

    setUploading(true);
    const urls: string[] = [];

    try {
      for (const file of files) {
        const result = await cloudinaryService.uploadWithSignature(
          file,
          {
            context: UploadContext.PET_GALLERY,
            ownerId: petId
          }
        );
        urls.push(result.secure_url);
      }

      setUploadedUrls([...uploadedUrls, ...urls]);
      onUploadSuccess(urls);

    } catch (err) {
      console.error('Upload failed:', err);
      alert('Some uploads failed. Please try again.');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="gallery-upload">
      <input
        type="file"
        accept="image/*"
        multiple
        onChange={handleMultipleFiles}
        disabled={uploading}
        id="gallery-input"
      />
      <label htmlFor="gallery-input" className="upload-button">
        {uploading ? 'Uploading...' : 'Add Photos'}
      </label>

      <div className="gallery-preview">
        {uploadedUrls.map((url, index) => (
          <img key={index} src={url} alt={`Gallery ${index}`} />
        ))}
      </div>
    </div>
  );
};
```

---

## Vanilla JavaScript Example

```html
<!DOCTYPE html>
<html>
<head>
  <title>Upload Avatar</title>
</head>
<body>
  <input type="file" id="fileInput" accept="image/*">
  <button id="uploadBtn">Upload Avatar</button>
  <div id="progress"></div>
  <img id="preview" style="display:none;">

  <script>
    const uploadBtn = document.getElementById('uploadBtn');
    const fileInput = document.getElementById('fileInput');
    const progress = document.getElementById('progress');
    const preview = document.getElementById('preview');

    uploadBtn.addEventListener('click', async () => {
      const file = fileInput.files[0];
      if (!file) {
        alert('Please select a file');
        return;
      }

      try {
        // Step 1: Get signature from backend
        const signResponse = await fetch('/api/v1/media/sign', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer YOUR_JWT_TOKEN'
          },
          body: JSON.stringify({
            context: 'USER_AVATAR',
            ownerId: 123
          })
        });

        const signData = await signResponse.json();

        // Step 2: Upload to Cloudinary
        const formData = new FormData();
        formData.append('file', file);
        formData.append('api_key', signData.api_key);
        formData.append('timestamp', signData.timestamp);
        formData.append('signature', signData.signature);
        formData.append('asset_folder', signData.asset_folder);
        formData.append('resource_type', signData.resource_type);
        
        if (signData.public_id) {
          formData.append('public_id', signData.public_id);
        }

        const uploadUrl = `https://api.cloudinary.com/v1_1/${signData.cloud_name}/${signData.resource_type}/upload`;
        
        const uploadResponse = await fetch(uploadUrl, {
          method: 'POST',
          body: formData
        });

        const uploadResult = await uploadResponse.json();

        // Step 3: Update avatar in your backend
        await fetch('/api/v1/users/me/avatar', {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer YOUR_JWT_TOKEN'
          },
          body: JSON.stringify({
            avatarUrl: uploadResult.secure_url
          })
        });

        // Show preview
        preview.src = uploadResult.secure_url;
        preview.style.display = 'block';
        alert('Upload successful!');

      } catch (error) {
        console.error('Upload failed:', error);
        alert('Upload failed: ' + error.message);
      }
    });
  </script>
</body>
</html>
```

---

## Vue.js Example

```vue
<template>
  <div class="avatar-upload">
    <div class="preview" v-if="previewUrl">
      <img :src="previewUrl" alt="Avatar">
    </div>

    <input
      type="file"
      ref="fileInput"
      accept="image/*"
      @change="handleFileSelect"
      :disabled="uploading"
    >

    <button @click="triggerFileInput" :disabled="uploading">
      {{ uploading ? 'Uploading...' : 'Choose Photo' }}
    </button>

    <div v-if="uploading" class="progress">
      <div class="progress-bar" :style="{ width: progress + '%' }"></div>
      <span>{{ Math.round(progress) }}%</span>
    </div>

    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { UploadContext } from '../types/media';

interface Props {
  userId: number;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  uploadSuccess: [url: string]
}>();

const fileInput = ref<HTMLInputElement | null>(null);
const uploading = ref(false);
const progress = ref(0);
const error = ref<string | null>(null);
const previewUrl = ref<string | null>(null);

const triggerFileInput = () => {
  fileInput.value?.click();
};

const handleFileSelect = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  if (!file) return;

  if (!file.type.startsWith('image/')) {
    error.value = 'Please select an image file';
    return;
  }

  try {
    uploading.value = true;
    error.value = null;
    progress.value = 0;

    // Get signature
    const signResponse = await fetch('/api/v1/media/sign', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getAuthToken()}`
      },
      body: JSON.stringify({
        context: UploadContext.USER_AVATAR,
        ownerId: props.userId
      })
    });

    const signData = await signResponse.json();

    // Upload to Cloudinary
    const formData = new FormData();
    formData.append('file', file);
    formData.append('api_key', signData.api_key);
    formData.append('timestamp', signData.timestamp.toString());
    formData.append('signature', signData.signature);
    formData.append('asset_folder', signData.asset_folder);
    formData.append('resource_type', signData.resource_type);
    
    if (signData.public_id) {
      formData.append('public_id', signData.public_id);
    }

    const uploadUrl = `https://api.cloudinary.com/v1_1/${signData.cloud_name}/${signData.resource_type}/upload`;
    
    const uploadResult = await uploadWithProgress(uploadUrl, formData);

    previewUrl.value = uploadResult.secure_url;
    emit('uploadSuccess', uploadResult.secure_url);

  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Upload failed';
  } finally {
    uploading.value = false;
    progress.value = 0;
  }
};

const uploadWithProgress = (url: string, formData: FormData): Promise<any> => {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();

    xhr.upload.addEventListener('progress', (e) => {
      if (e.lengthComputable) {
        progress.value = (e.loaded / e.total) * 100;
      }
    });

    xhr.addEventListener('load', () => {
      if (xhr.status === 200) {
        resolve(JSON.parse(xhr.responseText));
      } else {
        reject(new Error(`Upload failed: ${xhr.statusText}`));
      }
    });

    xhr.addEventListener('error', () => {
      reject(new Error('Upload failed'));
    });

    xhr.open('POST', url);
    xhr.send(formData);
  });
};

const getAuthToken = (): string => {
  return localStorage.getItem('authToken') || '';
};
</script>
```

---

## Error Handling

```typescript
// utils/errorHandler.ts
export class UploadError extends Error {
  constructor(
    message: string,
    public code?: string,
    public details?: any
  ) {
    super(message);
    this.name = 'UploadError';
  }
}

export const handleUploadError = (error: any): string => {
  if (error instanceof UploadError) {
    switch (error.code) {
      case 'MISSING_OWNER_ID':
        return 'User ID is required for this upload';
      case 'INVALID_SLUG_FORMAT':
        return 'Invalid identifier format';
      case 'MEDIA_SIGNATURE_GENERATION_FAILED':
        return 'Failed to generate upload signature. Please try again.';
      default:
        return error.message;
    }
  }

  if (error.message?.includes('NetworkError')) {
    return 'Network error. Please check your connection.';
  }

  return 'An unexpected error occurred. Please try again.';
};
```

---

## Best Practices

### 1. File Validation

```typescript
const validateFile = (file: File, options: {
  maxSize?: number;
  allowedTypes?: string[];
}): void => {
  const { maxSize = 5 * 1024 * 1024, allowedTypes = ['image/jpeg', 'image/png', 'image/webp'] } = options;

  if (!allowedTypes.includes(file.type)) {
    throw new Error(`File type ${file.type} is not allowed`);
  }

  if (file.size > maxSize) {
    throw new Error(`File size must be less than ${maxSize / 1024 / 1024}MB`);
  }
};
```

### 2. Image Optimization

```typescript
const getOptimizedUrl = (url: string, options: {
  width?: number;
  height?: number;
  quality?: number;
  format?: string;
}): string => {
  const { width, height, quality = 80, format = 'auto' } = options;
  
  // Insert transformations into Cloudinary URL
  const parts = url.split('/upload/');
  if (parts.length !== 2) return url;

  const transformations: string[] = [];
  if (width) transformations.push(`w_${width}`);
  if (height) transformations.push(`h_${height}`);
  transformations.push(`q_${quality}`);
  transformations.push(`f_${format}`);

  return `${parts[0]}/upload/${transformations.join(',')}/${parts[1]}`;
};

// Usage:
const thumbnailUrl = getOptimizedUrl(originalUrl, { width: 200, height: 200 });
const optimizedUrl = getOptimizedUrl(originalUrl, { width: 800, quality: 90, format: 'webp' });
```

### 3. Retry Logic

```typescript
const uploadWithRetry = async (
  file: File,
  request: MediaSignRequest,
  maxRetries = 3
): Promise<CloudinaryUploadResponse> => {
  let lastError: Error;

  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      return await cloudinaryService.uploadWithSignature(file, request);
    } catch (error) {
      lastError = error as Error;
      console.warn(`Upload attempt ${attempt} failed:`, error);
      
      if (attempt < maxRetries) {
        await new Promise(resolve => setTimeout(resolve, 1000 * attempt));
      }
    }
  }

  throw new Error(`Upload failed after ${maxRetries} attempts: ${lastError!.message}`);
};
```

---

**Last Updated:** January 2, 2026

