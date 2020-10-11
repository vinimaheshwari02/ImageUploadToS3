package com.example.ReactAndGraphQL.datastore;

import com.example.ReactAndGraphQL.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {
    public static List<UserProfile> USER_PROFILES=new ArrayList<>();

    static {
        USER_PROFILES.add(new UserProfile(UUID.fromString("aff15cde-8106-43f2-9d39-64e492ef5bf9"),"user1",null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("213a1251-9789-4da9-8369-100c790638b2"),"user2",null));
    }
    public List<UserProfile>getUserProfiles(){
        return USER_PROFILES;
    }
}
