FROM gcc:11.4.0
WORKDIR /usr/src/myapp
ARG FILE_DIRECTORY_ARG
ARG FILENAME_ARG
COPY ${FILE_DIRECTORY_ARG} /usr/src/myapp
RUN gcc -o myapp ${FILENAME_ARG}.c
ENV FILENAME_ENV=${FILENAME_ARG}
CMD ["sh", "-c", "sleep 1 && ./myapp"]


## Use the alpine base image
#FROM alpine:latest



#
#ARG FILE_DIRECTORY_ARG
#ARG FILENAME_ARG
#
#
#RUN apk update && \
#    apk add --no-cache gcc && \
#    apk add libc-dev && \
#    rm -rf /var/cache/apk/* &&
#
## Set the working directory inside the container
#WORKDIR /usr/src/myapp
#
#
#COPY ${FILE_DIRECTORY_ARG} /usr/src/myapp
#
## Compile the C program
#RUN gcc -o ${FILENAME_ARG} ${FILENAME_ARG}.c
#
## Run the executable when the container starts
#CMD ["sh", "-c", "./${FILENAME_ARG}"]