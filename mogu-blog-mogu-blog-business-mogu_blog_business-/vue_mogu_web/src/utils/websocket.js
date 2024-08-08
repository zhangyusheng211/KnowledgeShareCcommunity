import 'google-protobuf'
import message from '../proto/Message_pb.js'

export default {
  data () {
    return {
      username: '',
      email: '',
      content: '',
      message_list: [
        {
          email: '',
          content: ''
        }
      ],
      ws: null
    }
  },
  methods: {
    // 处理protobuf内容
    handleRecv: function (data) {
      // 这里对接收到的二进制消息进行解码
      var rep = message.Model.deserializeBinary(data)
      console.log(rep)
      // 可以获取data和code
      console.info(rep.getData())
      // 这里拼接消息，message_list是vue的一个列表，不要关心
      this.message_list.unshift({email: rep.getData().getEmail(), content: rep.getData().getContent()})
    },
    wsOpen: function () {
      var that = this
      var ws = new WebSocket('ws://localhost:8088')
      // 这个地方特别重要，websocket默认是Uint8array
      ws.binaryType = 'arraybuffer'

      ws.onopen = function () {
        console.info('ws open')
      }

      ws.onmessage = function (evt) {
        console.info(evt)
        console.info('Received message:' + evt.data)
        that.handleRecv(evt.data)
      }

      ws.onclose = function () {
        console.info('ws close')
      }

      this.ws = ws
    },
    wsSend: function () {
      if (this.ws == null) {
        console.info('连接尚未打开')
      }
      // 发送消息同样很简单，我们不需要关心格式
      var chat = new message.Model()
      chat.setCmd(3)
      chat.setSender('1')
      this.ws.send(chat.serializeBinary())
    }
  },
  mounted () {
    this.wsOpen()
  }
}
