# 道盲犬APP_配置说明
## 项目结构
#### 整体结构
![structure_1](https://raw.githubusercontent.com/LegendFC/YoloV3Tiny_Android_BlindNavigation/master/项目文档--必看/image/structure_1.png)
- OpenCV模块
- CPP模块
- Assets引入
- JAVA模块
- jniLibs引入
#### OpenCV模块
![structure_2](https://raw.githubusercontent.com/LegendFC/YoloV3Tiny_Android_BlindNavigation/master/项目文档--必看/image/structure_2.png)
- Version:3.4.0
#### CPP模块
![structure_3](https://raw.githubusercontent.com/LegendFC/YoloV3Tiny_Android_BlindNavigation/master/项目文档--必看/image/structure_3.png)
- darknet文件夹下存放着Yolo-v3-tiny的src、include、example等源代码
#### Assets引入
![structure_4](https://raw.githubusercontent.com/LegendFC/YoloV3Tiny_Android_BlindNavigation/master/项目文档--必看/image/structure_4.png)
- cfg、data、weights为Yolo-v3-tiny的配置、标签及权重
- 其余部分为TensorFlow for Android: Object Detection API的所需文件
#### JAVA模块
![structure_5](https://raw.githubusercontent.com/LegendFC/YoloV3Tiny_Android_BlindNavigation/master/项目文档--必看/image/structure_5.png)
- 项目的主体实现部分
#### jniLibs引入
![structure_6](https://raw.githubusercontent.com/LegendFC/YoloV3Tiny_Android_BlindNavigation/master/项目文档--必看/image/structure_6.png)
- 科大讯飞TTS SDK
## 环境配置
#### AS运行环境
- targetSdkVersion：28
#### 真机运行环境
![opencv](https://raw.githubusercontent.com/LegendFC/YoloV3Tiny_Android_BlindNavigation/master/项目文档--必看/image/opencv.png)
-   要求Android：9.0
-   首先安装.\Opencv_apk中的apk，根据自己的手机处理器架构型号，选择合适的apk
-   安装好后将刚刚安装的opencv开启，放至后台运行
-   安装运行道盲犬APP 
## Get Started
#### 环境说明
- 以下实验在HUAWEI MATE20 PRO，麒麟980环境下进行。
#### 盲道检测
- 盲道检测的fps达到15帧，基本达到实时标准。
#### 物体检测
- 物体检测的fps为2秒1帧，未达到实时标准。
- 因此，目前app采用图片模式处理。待模型进一步小型化后，再开启实时模式。