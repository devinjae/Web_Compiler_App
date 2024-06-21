FROM python:3.10.14-alpine
WORKDIR /usr/src/myapp
ARG FILE_DIRECTORY_ARG
ARG FILENAME_ARG
COPY ${FILE_DIRECTORY_ARG} /usr/src/myapp
ENV FILENAME_ENV=${FILENAME_ARG}
CMD ["sh", "-c", "sleep 1 && python ${FILENAME_ENV}.py"]

#FROM openjdk:11
#WORKDIR /usr/src/myapp
#COPY src/dir/sample /usr/src/myapp
#RUN javac Submission.java
#CMD ["java", "Submission"]