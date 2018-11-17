Welcome to the k-share-backend AWS CodeStar project
==============================================
This project contains following backend rest API created for k-share project.
 * GeneratePreSignedURL at http://127.0.0.1:3000/presigned [POST]
 * GetTopic at http://127.0.0.1:3000/topic [POST]
 * InitializeUsertable at http://127.0.0.1:3000/user [PATCH]
 * InitializeCommenTable at http://127.0.0.1:3000/topic/comment [PATCH]
 * resLambdaLocalCorsStub at http://127.0.0.1:3000/topic/{topic_id}/comment [OPTIONS]
 * CreateDocument at http://127.0.0.1:3000/doc [PUT]
 * resLambdaLocalCorsStub at http://127.0.0.1:3000/doc [OPTIONS]
 * CreateTopic at http://127.0.0.1:3000/topic [PUT]
 * UpdateTopic at http://127.0.0.1:3000/topic/{topic_id} [PUT]
 * resLambdaLocalCorsStub at http://127.0.0.1:3000/topic [OPTIONS]
 * InitializeDocumentTable at http://127.0.0.1:3000/doc [PATCH]
 * resLambdaLocalCorsStub at http://127.0.0.1:3000/presigned [OPTIONS]
 * CreateUser at http://127.0.0.1:3000/user [PUT]
 * GetAllCommentsForTopic at http://127.0.0.1:3000/topic/{topic_id}/comment [GET]
 * resLambdaLocalCorsStub at http://127.0.0.1:3000/user [OPTIONS]
 * InitializeTopicTable at http://127.0.0.1:3000/topic [PATCH]
 * GetUserByEmailId at http://127.0.0.1:3000/user/{email_Id}/{social_Site} [GET]
 * AddCommentToTopic at http://127.0.0.1:3000/topic/{topic_id}/comment [PUT]
 * GetDocument at http://127.0.0.1:3000/doc/{doc_id} [GET]
 

What's Here
-----------

This project includes:

* README.md - this file
* buildspec.yml - this file is used by AWS CodeBuild to build the web
  service
* pom.xml - this file is the Maven Project Object Model for the web service
* src/main - this directory contains your Java service source files
* src/test - this directory contains your Java service unit test files
* template.yml - this file contains the AWS Serverless Application Model (AWS SAM) used
  by AWS CloudFormation to deploy your application to AWS Lambda and Amazon API
  Gateway.


What Do I Do Next?
------------------

If you have checked out a local copy of your repository you can follow below steps to start
Running this project locally
* Install docker locally following https://store.docker.com/editions/community/docker-ce-desktop-mac
* Setup SAM-Local for your eclipse by following https://aws.amazon.com/blogs/developer/aws-toolkit-for-eclipse-locally-debug-your-lambda-functions-and-api-gateway/
* Install dynamodb locally by following https://hub.docker.com/r/amazon/dynamodb-local/
* Follow instruction here  https://aws.amazon.com/blogs/developer/aws-toolkit-for-eclipse-locally-debug-your-lambda-functions-and-api-gateway/ . to run and debug rest endpoints locally.



Learn more about Maven's [Standard Directory Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html).

Learn more about managing Maven dependencies with AWS SDK for Java using the
[Bill of Materials Module](https://aws.amazon.com/blogs/developer/managing-dependencies-with-aws-sdk-for-java-bill-of-materials-module-bom/).

Learn more about AWS CodeBuild and how it builds and tests your application here:
https://docs.aws.amazon.com/codebuild/latest/userguide/concepts.html

Learn more about AWS Serverless Application Model (AWS SAM) and how it works here:
https://github.com/awslabs/serverless-application-model/blob/master/HOWTO.md

AWS Lambda Developer Guide:
http://docs.aws.amazon.com/lambda/latest/dg/deploying-lambda-apps.html

Learn more about AWS CodeStar by reading the user guide, and post questions and
comments about AWS CodeStar on our forum.

User Guide: http://docs.aws.amazon.com/codestar/latest/userguide/welcome.html

Forum: https://forums.aws.amazon.com/forum.jspa?forumID=248



Best Practices: https://docs.aws.amazon.com/codestar/latest/userguide/best-practices.html?icmpid=docs_acs_rm_sec
