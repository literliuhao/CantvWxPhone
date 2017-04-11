# Question & Answer

* 已解决问题

1. 超大图加载导致缓慢卡顿
解决方案：


2.某些手机上传原图或较大的图片后，图片加载很慢并且旋转卡顿
解决方案：setDecodeFormat(DecodeFormat decodeFormat).
     设置解码器解码格式。
     之前使用的是DecodeFormat.PREFER_ARGB_8888。修改为DecodeFormat.PREFER_RGB_565，因为相对于ARGB_8888的4字节/像素可以节省一半的内存。
     （崩溃问题同上解决）

* 未解决问题

1.加载超长图无法显示：override尺寸改为240*320可以显示超长图，在大一点就无法显示。

2.


