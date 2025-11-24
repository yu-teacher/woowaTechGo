FROM nvidia/cuda:11.8.0-runtime-ubuntu22.04

# Java + NVIDIA OpenCL 설치
RUN apt-get update && apt-get install -y \
    openjdk-21-jre-headless \
    nvidia-opencl-dev \
    ocl-icd-libopencl1 \
    clinfo \
    libzip4 \
    libstdc++6 \
    libgcc-s1 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 9001

CMD ["java", "-jar", "app.jar"]