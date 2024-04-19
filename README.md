[ ![Github](https://img.shields.io/badge/bihe0832-AndroidAppFactory-brightgreen?style=social) ](https://github.com/bihe0832/AndroidAppFactory)
[ ![Github](https://img.shields.io/github/last-commit/bihe0832/AndroidAppFactory) ](https://github.com/bihe0832/AndroidAppFactory)
[ ![Github](https://img.shields.io/github/stars/bihe0832/AndroidAppFactory?style=social) ](https://github.com/bihe0832/AndroidAppFactory)
[ ![Github](https://img.shields.io/bitbucket/issues/bihe0832/AndroidAppFactory) ](https://github.com/bihe0832/AndroidAppFactory)

## 相关链接

- **框架文档：**

    - 链接：[https://android.bihe0832.com/doc/](https://android.bihe0832.com/doc/)
    
    - 内容：主要**介绍相关组件的功能以及整体框架使用相关的内容**

- 技术方案介绍：

    - 链接：[https://blog.bihe0832.com/android-dev-summary.html](https://blog.bihe0832.com/android-dev-summary.html)
    
    - 内容：完整AAF的整个技术方案，以及其中一些核心的技术点
	

## 框架能力

**框架目前已经有基础组件 65 + 个，公共组件 30 + 个，墙裂建议框架的完整能力查看框架使用文档**：[https://android.bihe0832.com/doc/](https://android.bihe0832.com/doc/use/common/common-wrapper.html) ，此处仅列出部分支持能力 ：

- **单Activity ＋ 多Fragment 模式的Fragment管理框架、各种样式的TabLayout、沉浸式状态栏和导航栏及设置背景色和文字颜色、简易文件选择器、文本查看器**。使用系统原生对Activity进一步封装扩展的的拍照、相册选择、图片的裁剪、旋转、缩放，二维码生成与识别，分享，绘图板、颜色选择等。以及通用闪屏、侧边栏，关于页面等，隐私合规，隐私协议等

- **跨进程调用、定时任务管理、配置管理、线程管理及线程池、协程调用封装**

- **权限管理及申请**，检查权限的引导弹框、支持实现自定义界面，打开指定应用设置

- **下载功能**：支持文件的完整下载与区间下载。下载过程支持分片下载、断点续传、多线程高速下载、已下载文件校验、下载详细回调、可控下载是否排队、是否通知栏提醒、下载后完整性校验、下载异常自动重试、下载增加优先级，再队列已经满的情况下，部分高优先级文件依然可以下载。**应用安装**，支持包括OBB、Split APK等形式安装包

- 基于X5 定制的**Webview 及 JSbridge 相关实现**、封装好的**通用的 Webview 的Fragment**，支持：下拉刷新，请求追加业务参数，错误页面及错误重试，非 Http 协议使用 Intent 唤起，获取网页标题，将终端的前后台切换响应到H5

- RecycleView 相关的**动态列表Adapter、下拉刷新、上滑加载**、添加分割边框、获取当前可见区域内首个或者最后一个可见、完整可见等情况下的元素、滑动结束回调最新的状态

- 通用的Toast 弹框、可定制Toast，上、下、左、右方位以及距离页边距的距离、支持基础的HTML内容

- **通用的Dialog**：支持标题、内容、按钮动态设置。**下载进度弹框、输入弹框、Loading 弹框、列表单选弹框、图片展示弹框、底部弹框、底部列表弹框等**。在指定位置弹菜单列表、快捷操作等，通用通知栏封装：下载进度通知、检查通知栏权限，拉起通知栏授权、内容不断刷新的通知栏

- 生命周期管理，获取**应用相关的基本信息**，例如：应用安装时间、当前版本安装时间、上次启动时间、最后一次启动版本、应用使用天数、使用次数、当前版本使用次数；获取**生命周期相关的信息**，例如：当前应用是否前后台、上次切后台时间、上次回到前台时间、本次启动时间

- **获取设备信息**：例如分辨率、屏幕宽高获取、DP等单位转换，获取设备电量、充电状态、各种设备ID、厂商品牌等（包括是否指定厂商，厂商系统版本），获取是否包含SIM卡，网络是否开启、获取对上网卡的运营商信息、获取信号强度、网络是否可用、周边基站信息、当前网络状态（类型、强度、可用性、IP）IP合法性判断，DNS解析、获取Wi-Fi的各种信息

- **数据中心**，支持数据缓存，定义了远程数据拉取方式、本地数据缓存时间长短等，支持协程。基于 SQLiteOpenHelper 封装的 key-value 存储数据库提供，数据插入、查询、删除的基本功能

- **文件管理**（检查文件存在、创建多级文件夹、获取文件长度、内容、文件名、删除文件及文件夹、滑动读取写入文件、Assets管理、**FileProvider 封装，获取FileProvider 对应文件地址、路径、根据文件生成对应的URI**），**Zip 文件压缩与解压缩**（支持超过2G文件）、不解压查看文件列表，解压指定文件，**AES、MD5、SHA256、计算及GZIP压缩解压**，

- 使用HTTPURLConnection 通过Http 或者 HTTPS 同步、异步发送GET和POST请求并返回指定数据结构，文件上传

- 图像、音视频等的处理，例如：Bitmap相关的各种处理：裁剪、压缩、旋转、圆角、叠加等、**保存到本地、合并拼接、添加浮层、获取本地、远程文件的Bitmap、高斯模糊**。文字生成图片，生成音频，图片音频合成视频。

- 支持**动态修改 TextView 背景**，如背景色、边框、弧度等，扩展TextView用于**扩展收起、展开、固定行尾字符**等以及TextView 的基础扩展，可以更方便的展示更复杂的文字组合如部分支持点击、内嵌图标、部分内容圆角背景等。

- 特定场景的特殊功能，例如UI全局置灰、锁频界面，widget，一键换肤等

- 丰富完备的调试功能，同时提供打开网页、弹出输入框、跳转Activity、基于 LibDebug 的调试信息文本分享等基础功能、通用调试方法，包括**查看应用版本及环境、使用情况，查看设备信息，第三方应用信息**，以及一些快速跳转的功能入口

