package org.example.healbackend.bean;

public class Consultant {
   private int consultant_id;
   private int user_id;
   private String name;
   private String avatar;
   private String description;
   private Boolean is_online;

   public Boolean getIs_online() {
      return is_online;
   }

   public void setIs_online(Boolean is_online) {
      this.is_online = is_online;
   }

   public Consultant(int consultant_id, int user_id, String name, String avatar, String description, Boolean is_online) {
      this.consultant_id = consultant_id;
      this.user_id = user_id;
      this.name = name;
      this.avatar = avatar;
      this.description = description;
      this.is_online = is_online;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getConsultant_id() {
      return consultant_id;
   }

   public void setConsultant_id(int consultant_id) {
      this.consultant_id = consultant_id;
   }

   public int getUser_id() {
      return user_id;
   }

   public void setUser_id(int user_id) {
      this.user_id = user_id;
   }

   public String getAvatar() {
      return avatar;
   }

   public void setAvatar(String avatar) {
      this.avatar = avatar;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
}
