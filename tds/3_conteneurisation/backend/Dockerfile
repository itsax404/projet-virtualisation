FROM debian:latest

RUN apt update -y
RUN apt upgrade -y
RUN apt install bash htop vim net-tools -y

ENV OWNER=Quentin

RUN useradd nonroot
USER nonroot


CMD ["sleep", "3600"]