package questionStream;
import com.google.gson.annotations.SerializedName;

public class Question {
        @SerializedName("sid")
        private String sid; //session id extracted from socket
        @SerializedName("title")
        private String title;
        @SerializedName("content")
        private String content;
        @SerializedName("tags")
        private String[] tags;
        @SerializedName("timestamp")
        private String timestamp;

        public void setTags(String[] tags) {
            this.tags = tags;
        }

        public String getTitle() {
            return this.title;
        }

        public String getContent() {
            return this.content;
        }

        public String[] getTags() {
            return this.tags;
        }

        public String getSid() {
            return this.sid;
        }

        public String getTimeStamp() {
            return this.timestamp;
        }
}
