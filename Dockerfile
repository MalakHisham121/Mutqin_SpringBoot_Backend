FROM ubuntu:latest
LABEL authors="Malk"

ENTRYPOINT ["top", "-b"]