FROM rabbitmq:4-management

ENV RABBITMQ_USER=admin 
ENV RABBITMQ_PASSWORD=admin 
ENV RABBITMQ_PID_FILE=/var/lib/rabbitmq/mnesia/rabbitmq

ADD init.sh /init.sh
RUN chmod +x /init.sh 

EXPOSE 15672
EXPOSE 5672

CMD ["/init.sh"]