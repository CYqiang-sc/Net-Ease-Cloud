// 云对象
const FormData = require('form-data'); // 此form-data需要使用npm安装，地址：https://www.npmjs.com/package/form-data

module.exports = {
	// 检测是否有敏感词
	checkText: async function(access_token, content) {
		const res = await uniCloud.httpclient.request(
			'https://api.weixin.qq.com/wxa/msg_sec_check?access_token=' + access_token, {
				method: 'POST',
				data: {
					content: content
				},
				contentType: 'json', // 指定以application/json发送data内的数据
				dataType: 'json' // 指定返回值为json格式，自动进行parse
			})

		if (res.status === 200 && res.data.errcode === 0) {
			return {
				code: 0,
				msg: '没有敏感词'
			}
		} else {
			return {
				code: -1,
				msg: '含有敏感词'
			}
		}
	},
	// 检测图片是否有敏感信息
	checkImg: async function(access_token, img) {
		const form = new FormData()
		let bf = Buffer.from(img, 'base64')
		form.append('media', bf, Date.now() + '.jpg')
		const res = await uniCloud.httpclient.request(
			'https://api.weixin.qq.com/wxa/img_sec_check?access_token=' + access_token, {
				method: 'POST',
				content: form.getBuffer(),
				headers: form.getHeaders(), // 请求头
				dataType: 'json' // 指定返回值为json格式，自动进行parse
			})
		if (res.status === 200 && res.data.errcode === 0) {
			return {
				code: 0,
				msg: '没有敏感图片'
			}
		} else {
			return {
				code: -1,
				msg: '含有敏感图片'
			}
		}
	}
}
