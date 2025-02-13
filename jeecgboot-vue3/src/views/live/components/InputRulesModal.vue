<template>
  <BasicModal @register="register" width="1000px" :canFullscreen="false">
    <div class="rules-container">
      <div class="rules-content">
        <h1 class="rules-title">音视频输入源规则</h1>

        <section class="rule-section">
          <h2>HTTP 协议</h2>
          <p>支持 http/https 的来源输入流，可以是点播也可以是 http-flv/http-ts/http-fmp4 等类型的直播来源。</p>
          <div class="rule-details">
            <p>http类型来源流，将默认使用循环，以便在播放完毕后自动循环使用，同时将默认使用播放预读机制-re，如果来源流使用播放预读会出现输出卡顿等情况，可在URL中添加re=0参数来禁止，如：</p>
            <div class="code-block">http://www.test.com/video/x.mp4?re=0</div>
            <p>在多输入源的时候，短的视频点播素材在直接在线获取循环的时候会导致任务不停重启，此时需要将素材进行服务器下载使用，为URL增加download=1参数来启用下载使用，如：</p>
            <div class="code-block">http://www.test.com/video/x.mp4?download=1</div>
          </div>
        </section>

        <section class="rule-section">
          <h2>RTMP 协议</h2>
          <p>支持 rtmp 系列协议的来源输入流，一般此协议都是直播来源，如果来源于 LiveSphere Dream 直播平台，需要为URL添加dream=1参数来启用拉取，如：</p>
          <div class="code-block">rtmp://192.168.1.20/live/tv_stream?dream=1</div>
        </section>

        <section class="rule-section">
          <h2>RTSP 协议</h2>
          <p>支持 rtsp 系列协议来源输入流，一般此协议为直播来源，多来自于摄像头设备，支持基于 tcp/udp 的rtsp流。</p>
          <div class="rule-details">
            <p>大部分时候，rtsp协议拉取会自动选择承载协议，当需要选择承载接口的时候，为URL使用rtsp_transport参数来定义，可选的值有udp、tcp、http，如：</p>
            <div class="code-block">rtsp://192.168.1.20:554/live/tv_stream?rtsp_transport=tcp</div>
            <ul class="c_ul">
              <li>数据量很大的时候，可以为URL使用reorder_queue_size参数来设置缓冲区的数据包数量</li>
              <li>同时可以为URL使用stimeout参数，设置 TCP I/O 的连接超时，单位为微秒</li>
            </ul>
          </div>
        </section>

        <section class="rule-section">
          <h2>UDP 协议</h2>
          <p>支持 UDP 系列协议，UDP流一般在内网使用，需要三层交换设备环境支持，分为单播和组播方式，如果为组播流需要提前确认服务器上网口的路由表设置是否正确。</p>
          <div class="rule-details">
            <ul class="c_ul">
              <li>当为输入源的时候，udp流默认使用overrun_nonfatal=1参数启用接收循环缓冲区</li>
              <li>当为输入源的时候，udp流默认使用fifo_size参数设置接收缓冲区大小，也可在URL中自定义大小，单位 bytes</li>
              <li>当为输出的时候，udp 输出默认使用buffer_size参数设置发送缓冲区大小，也可在URL中自定义大小，单位 bytes</li>
              <li>当为输出的时候，udp 输出默认使用pkt_size参数发送包大小，默认1316，也可在URL中自定义大小，单位 bytes</li>
              <li>当为输出的时候，udp 流发送数据包会根据缓冲区自动执行，可以为URL使用flush_packets=1参数，启用强制按照pkt_size设置大小发送数据包</li>
            </ul>
            <p>使用参数的URL示例：</p>
            <div class="code-block">
              udp://238.0.0.1:3000?overrun_nonfatal=1&fifo_size=557755&buffer_size=5000000&pkt_size=1316&flush_packets=1
            </div>
          </div>
        </section>

        <section class="rule-section">
          <h2>图片输入源规则</h2>
          <p>支持 http/https 协议的图片输入源地址，系统通过文件的后缀来判断是否为图片，图片后缀为.jpg、.png、.jpeg、.webp、.gif</p>
          <div class="rule-details">
            <ul class="c_ul">
              <li>图片源将默认使用循环输入方式</li>
              <li>且默认启用服务器下载使用方式</li>
            </ul>
          </div>
        </section>
      </div>
    </div>
  </BasicModal>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
import { BasicModal, useModalInner } from '/@/components/Modal';
export default defineComponent({
  components: { BasicModal },
  setup() {
    const [register] = useModalInner();
    return {
      register
    };
  },
});
</script>

<style>
.rules-container {
  padding: 24px;
  background: rgba(30, 30, 30, 0.7);
  min-height: 100vh;
  box-sizing: border-box;
  backdrop-filter: blur(20px);
  color: #fff;
}

.rules-content {
  max-width: 1000px;
  margin: 0 auto;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.rules-title {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 32px;
  text-align: center;
  background: linear-gradient(45deg, #fff, #ccc);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.rule-section {
  margin-bottom: 32px;
}

.rule-section h2 {
  font-size: 20px;
  font-weight: 500;
  margin: 0 0 16px;
  color: #409EFF;
  display: flex;
  align-items: center;
  gap: 8px;
}

.rule-section h2::before {
  content: '';
  display: block;
  width: 4px;
  height: 20px;
  background: #409EFF;
  border-radius: 2px;
}

.rule-details {
  margin-top: 16px;
  padding-left: 16px;
  border-left: 2px solid rgba(64, 158, 255, 0.2);
}

.code-block {
  background: rgba(0, 0, 0, 0.3);
  padding: 12px 16px;
  border-radius: 6px;
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 13px;
  color: #E6A23C;
  margin: 12px 0;
  word-break: break-all;
  position: relative;
  overflow-x: auto;
}

.code-block:hover {
  background: rgba(0, 0, 0, 0.4);
}

.c_ul {
  list-style: none;
  padding-left: 0;
  margin: 12px 0;
}

.c_ul li {
  position: relative;
  padding-left: 20px;
  margin-bottom: 8px;
  line-height: 1.6;
}

.c_ul li::before {
  content: '•';
  color: #409EFF;
  position: absolute;
  left: 0;
  top: 0;
}

.c_p {
  line-height: 1.6;
  margin: 12px 0;
  color: rgba(255, 255, 255, 0.9);
}
</style>