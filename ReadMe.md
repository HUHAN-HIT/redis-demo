在服务依赖中加入Reactive Redis可能会导致加密套件不匹配的问题，需要对服务器端的加密套件进行相应的配置，以确保服务器端支持Reactive Redis使用的加密套件。

在Spring Boot中，可以通过在application.properties文件中添加以下属性来配置Redis支持的加密套件：

bash
Copy code
# 指定SSL的密钥库文件和密码
'''
server.ssl.key-store=classpath:ssl/keystore.p12
server.ssl.key-store-password=password
'''

# 指定允许的协议和加密套件
server.ssl.enabled-protocols=TLSv1.2,TLSv1.3
server.ssl.ciphers=TLS_AES_128_GCM_SHA256,TLS_AES_256_GCM_SHA384
在这个例子中，我们使用TLS_AES_128_GCM_SHA256和TLS_AES_256_GCM_SHA384作为Redis支持的加密套件，与TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256和TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384不同。这些加密套件支持AES加密算法，比较安全。

如果服务器端配置了支持的加密套件，但仍然无法解决问题，可能需要检查服务器端和Redis依赖的版本是否兼容。如果还存在问题，可以尝试查看服务器端或Redis依赖的日志，以便更进一步的排查。




