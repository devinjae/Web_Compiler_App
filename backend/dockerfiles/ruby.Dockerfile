FROM ruby:alpine3.20
ARG FILE_DIRECTORY_ARG
ARG FILENAME_ARG
RUN bundle config --global frozen 1
WORKDIR /usr/src/myapp
COPY ${FILE_DIRECTORY_ARG} /usr/src/myapp
ENV FILENAME_ENV=${FILENAME_ARG}
CMD ["sh", "-c",  "sleep 1 && ruby ${FILENAME_ENV}.rb"]
