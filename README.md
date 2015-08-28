fir.im upload
---
> 一键上传应用到fir.im

> jetbrains插件线上地址 [fir.im upload](https://plugins.jetbrains.com/plugin/7640?pr=androidstudio)

> 插件下载地址 [fir.im-upload-2.0](http://firweb.fir.im/fir_plugin_2.0.0.zip) (下载之后使用本地安装 --添加了展示二维码的功能)

> 插件下载地址 [fir.im-upload-1.9](http://firweb.fir.im/fir_plugin_1.9.0.zip) (下载之后使用本地安装)



## 使用入门
### 从安装入手

在plugins插件市场搜索fir.im upload安装就可以了:现在支持平台 Android Studio & IntelliJ IDEA

安装之后 重启对应的开发环境

插件会在Views -> tool windows显示 FIR.im

### 具体安装步骤
fir.im upload 安装
- 方式一、 Android Studio-> preferences -> plugins -> 搜索fir.im upload
- 方式二、[下载包进行本地安装](http://firweb.fir.im/fir_plugin_1.9.0.zip)，步骤 Android Studio-> preferences -> plugins -> install plugin from disk..

### 使用说明

打开插件先设置api_token,查看[api_token](http://fir.im/user/info)

插件会自动检测当前功能的apk路径（如果没有检测到apk则可以手动设置apk路径）

可以填写更新日志

检测提示复选框 -> 意思是 当你选中检测提示复选框，当发现apk有改变时就会直接提示是否上传，取消则不提示

自动上传复选框 -> 意思是 当你选中自动上传复选框，会自动检测apk改变时直接上传，取消则不自动上传


## 提交反馈

[使用 github issue 即可](https://github.com/FIRHQ/fir_intellig_plugin/issues)
