package com.example.ReactAndGraphQL.Buckets;

public enum BucketName {
    PROFILE_IMAGE("vini-project-image-upload-123");

    private final String bucketName;

    BucketName(String bucketName){
        this.bucketName=bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}

