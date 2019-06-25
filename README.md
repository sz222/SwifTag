# Real-Time Auto Tagging System 

**_-- Author: Shuyan(Stephanie) Zhou_**

Insight Data Engineering Fellowship Program Project.
This project provide real-time auto tagging suggestions for users that post questions on StackOverflow according to the question title.


### Project Idea:
  High quality data is essential in data mining and product analytics work, for questions related data, if "tag" information could be appended appropriately. Data scientists can perform user identification & behavior pattern recognization better. 
  
  However, not all the users can choose tags appropriately for their questions due to several reasons: 1) questions could be complicated and cover several areas that users don't know how to choose appropirate tags   2) sometimes users are just forget or lazy to attach tags. 
  
 ### Business Case: 
 In this project, we can provide real-time __top 3 tags__ recommendation after end users type in question title. We also provide end users options to delete or add tags based on their preference. In this way, data scientists can get better quality question data and use it perform clustering, user behavior investigation and recommendation model evaluation better.
 
 ### Demo:
![Auto Tagging System](https://github.com/watermelonsz/AutoTagging/blob/master/Presentation/autoTagging.gif)
 
 ### Data:
 __Source:__ BigQuery Open Datasets\
 __Table Name:__ [Posts Questions](https://bigquery.cloud.google.com/table/bigquery-public-data:stackoverflow.posts_questions)\
 __Data Description:__  id, body, comment_count, community_owned_date, creation_date, last_activity_date, last_edit_date, last_editor_display_name, last_editor_user_id, owner_display_name, owner_user_id, parent_id, post_type_id, score, tags
 
### Tech Stack:

![tech stack image](https://raw.githubusercontent.com/watermelonsz/AutoTagging/test/Presentation/tech_stack.png)

  
