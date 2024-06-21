#FROM esolang/cpp-clang:2.4.0
#WORKDIR /usr/src/myapp
#ARG FILE_DIRECTORY_ARG
#ARG FILENAME_ARG
#COPY ${FILE_DIRECTORY_ARG} /usr/src/myapp
#ENV FILENAME_ENV=${FILENAME_ARG}
#CMD ["sh", "-c", "sleep 1 && dir && cpp-clang ${FILENAME_ENV}.cpp"]

FROM gcc:11.4.0
WORKDIR /usr/src/myapp
ARG FILE_DIRECTORY_ARG
ARG FILENAME_ARG
COPY ${FILE_DIRECTORY_ARG} /usr/src/myapp
RUN gcc -o myapp ${FILENAME_ARG}.cpp -lstdc++
ENV FILENAME_ENV=${FILENAME_ARG}
CMD ["sh", "-c", "sleep 1 && ./myapp"]
