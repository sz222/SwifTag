package questionStream;
import com.google.gson.annotations.SerializedName;
/*
** This java file constucts Question class based on each question data
*/
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
        
        //this function sets tags for the question
        public void setTags(String[] tags) {
            this.tags = tags;
        }
        //this function gets title for the question
        public String getTitle() {
            return this.title;
        }
        //this function gets question body from question data
        public String getContent() {
            return this.content;
        }
        //this function get tags for the question
        public String[] getTags() {
            return this.tags;
        }
        //this function get session id for the questions
        public String getSid() {
            return this.sid;
        }
        //this function get timestamp for the question
        public String getTimeStamp() {
            return this.timestamp;
        }
}
