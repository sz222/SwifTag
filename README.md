#  SwifTag          


## -- A real-time auto tagging system 
**_Author: Stephanie(Shuyan) Zhou_**
 

Insight Data Engineering Fellowship Program Project.
This project provides a real-time auto tagging pipeline for users that post questions on StackOverflow.

### Gif Demo:
![Auto Tagging System](https://raw.githubusercontent.com/watermelonsz/SwifTag/master/Presentation/swifTag.gif)
### Presentation Slides:
[SwifTag Slides](https://docs.google.com/presentation/d/1WvAQQqIJ-ozDySBpCqi31Ao3Wk-f-X_I/edit#slide=id.g5c6d79aff0_0_119)
### Live Website:
[SwifTag Website](http://www.dataexplorer.club/)
### Demo Video:
[SwifTag Demo Video](https://www.youtube.com/watch?v=38nNJJwNtyQ&feature=youtu.be)
### Presentation Video:
[SwifTag Presentation Video](https://www.youtube.com/watch?v=jHk11314wW0&feature=youtu.be)


### Project Idea:
  High quality data is essential in data mining and product analytics work, for questions related data, if "tag" information could be appended appropriately. Data scientists can perform user identification & behavior pattern recognization better. 
  
  However, not all the users can choose tags appropriately for their questions due to several reasons: 1) questions could be complicated and cover several areas that users don't know how to choose appropirate tags   2) sometimes users are just forget or lazy to attach tags. 
  
 ### Business Case: 
 In this project, we can provide real-time __top 3 tags__ recommendation after end users type in question title. We also provide end users options to delete or add tags based on their preference. In this way, data scientists can get better quality question data and use it perform clustering, user behavior investigation and recommendation model evaluation better.
 
 ### Data:
 __Source:__ BigQuery Open Datasets\
 __Table Name:__ [Posts Questions](https://bigquery.cloud.google.com/table/bigquery-public-data:stackoverflow.posts_questions)\
 __Data Description:__  id, body, comment_count, community_owned_date, creation_date, last_activity_date, last_edit_date, last_editor_display_name, last_editor_user_id, owner_display_name, owner_user_id, parent_id, post_type_id, score, tags
 
### Tech Stack:
This project includes both __Batch Processing__ and __Streaming Processing__:

![tech stack image](https://raw.githubusercontent.com/watermelonsz/SwifTag/master/Presentation/swifTag.png)

  
