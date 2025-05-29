FROM openjdk:17-alpine
WORKDIR /usr/src/myapp
ARG FILE_DIRECTORY_ARG
ARG FILENAME_ARG
COPY ${FILE_DIRECTORY_ARG} /usr/src/myapp
RUN javac ${FILENAME_ARG}.java
ENV FILENAME_ENV=${FILENAME_ARG}
CMD ["sh", "-c",  "sleep 1 && java ${FILENAME_ENV}"]

#COPY ./code /usr/src/myapp
#RUN javac Submission.java
#CMD ["java", "Submission"]

