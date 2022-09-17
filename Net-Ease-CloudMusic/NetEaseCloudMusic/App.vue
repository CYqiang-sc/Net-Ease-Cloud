<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		},
		globalData: {
			innerAudioContext: uni.createInnerAudioContext()
		},

	}
</script>

<style>
	/*每个页面公共css */
	@import "@/common/uni.css";
	@import "./static/iconfont/iconfont.css";
	@import "./static/iconfont/icon.css";

	uni-page-body,
	html,
	body {
		height: 100%;
	}
</style>
