package com.example.ReactAndGraphQL.profile;

import com.example.ReactAndGraphQL.Buckets.BucketName;
import com.example.ReactAndGraphQL.FileStore.FileStore;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UserProfileService {
    private final UserProfileDataAccessService userProfileDataAccessService;

    private final FileStore fileStore;

    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore fileStore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;
    }

    List<UserProfile> getUserProfiles(){
        return userProfileDataAccessService.getUserProfiles();
    }


    public void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
        //check if image is empty
        if(file.isEmpty() || file.getSize()==0){
            throw new IllegalStateException("Cannot upload empty file");
        }
        //the file is an image
        checkFileFormat(file);
        //the user exist in database
        UserProfile user= getUserProfile(userProfileId);

        //grab some metadata from file if any

        Map<String, String> metaData = extractMetaData(file);

        //store image in s3 and update database(userProfileImageLink) with s3 image link
        String path=String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(),userProfileId);
        String fileName=String.format("%s-%s",file.getOriginalFilename(),UUID.randomUUID());
        try {
            fileStore.save(path,fileName,Optional.of(metaData),file.getInputStream());
            user.setUserProfileImageLink(fileName);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    private Map<String, String> extractMetaData(MultipartFile file) {
        Map<String,String> metaData=new HashMap<>();
        metaData.put("Content-Type", file.getContentType());
        metaData.put("Content-Length",String.valueOf(file.getSize()));
        return metaData;
    }

    private void checkFileFormat(MultipartFile file) {
        if(!Arrays.asList(ContentType.IMAGE_JPEG.getMimeType(),ContentType.IMAGE_PNG.getMimeType()).contains(file.getContentType())){
            throw new IllegalStateException("file must be image");
        }
    }

    private UserProfile getUserProfile(UUID userProfileId) {
        return userProfileDataAccessService.getUserProfiles()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User doesnot exist with id" + userProfileId));
    }

    public byte[] downloadUserProfileImage(UUID userProfileId) {
        UserProfile userProfile=getUserProfile(userProfileId);

        String path=String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(),userProfileId);
        return userProfile.getUserProfileImageLink()
                .map(key->fileStore.download(path,key))
                .orElse(new byte[0]);

    }
}
