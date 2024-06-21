FROM golang:alpine3.20
WORKDIR /usr/src/myapp
ARG FILE_DIRECTORY_ARG
ARG FILENAME_ARG
COPY ${FILE_DIRECTORY_ARG} /usr/src/myapp
RUN go mod init /docker
RUN go mod download
RUN god build -o ${FILENAME_ARG}
ENV FILENAME_ENV=${FILENAME_ARG}
CMD ["sh", "-c",  "sleep 1 && ls && ${FILENAME_ENV}.rb"]