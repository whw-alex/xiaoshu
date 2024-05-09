package com.example.xiaoshu.Response;

public class UserResponse {
    private String username;
    private String avatar;
    private int id;
     public UserResponse(String username, String avatar, int id) {
         this.username = username;
         this.avatar = avatar;
         this.id = id;
     }
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getAvatar() {
            return avatar;
        }
        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }

}
