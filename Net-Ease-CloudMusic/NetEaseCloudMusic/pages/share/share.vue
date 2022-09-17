<template>
	<view>
		<!-- 分享文字 -->
		<view class="uni-textarea">
			<textarea auto-height maxlength=140 placeholder="用文字写下此刻心情, 会被更多人看到哦" v-model="text" />
			<button class="mini-btn" type="warn" size="mini" @click="share()">分享</button>
		</view>

		<!-- 分享的内容 -->
		<view class="content">
			<view class="cover">
				<image :src="coverImg" mode=""></image>
			</view>
			<text class="name">{{name}}</text>
		</view>
	</view>
</template>

<script>
	import {
		msgSecCheck,
		getTaken
	} from "../../utils/request.js"
	export default {
		data() {
			return {
				text: "",
				id: "",
				coverImg: "",
				name: "",
				type: "",
				taken: "",
				code: '',
				openid: '',
				result: {}
			}
		},
		onLoad(e) {
			this.openid = uni.getStorageSync("openid")
			this.id = e.id;
			this.type = e.type;
			console.log(this.id, this.type);
			this.getBaseInfo()
			getTaken('wx90d3328edfe8f704', '7aa7c0374dc3af309e5e8629d2ff531c', res => {
				this.taken = res.access_token
				console.log(res)
			})
		},
		methods: {
			// 分享
			share() {
				msgSecCheck(
					this.taken, {
						"openid": this.openid,
						"scene": 2,
						"version": 2,
						"content": this.text
					},
					res => {
						console.log(res)
						if (res.errcode==0) {
							this.result = res.result
							switch (this.result.label) {
								case 100:
									uni.request({
										url: this.$apiBaseUrl.apiBaseUrl + "/share/resource",
										data: {
											id: this.id,
											msg: this.text,
											// type:this.ty
										}
									}).then(res => {
										res = res[1].data;
										console.log(res);
										if (res.code == 200) {
											uni.showToast({
												title: "分享成功",
												duration: 2000
											})
											// 跳转到我的动态页面
											uni.navigateTo({
												url: "/pages/my/child/myupdates"
											})
										}
									});

									console.log("正常");
									break;
								case 10001:
									console.log("广告");
									break;
								case 20001:
									console.log("政治敏感话题");
									break;
								case 20002:
									console.log("包含色情信息");
									break;
								case 20003:
									console.log("包含辱骂语句");
									break;
								case 20006:
									console.log("违法犯罪信息");
									break;
								case 20008:
									console.log("欺诈信息");
									break;
								case 20008:
									console.log("低俗信息");
									break;
								case 20008:
									console.log("版权问题");
									break;
								case 20008:
									console.log("其他");
									break;
								default:
							}
						}else{
							uni.showToast({
								title: res.errmsg,
								icon: "none"
							})
						}
						// console.log(res,222)
					}, res => {
						// console.log(res,333)
					}
				)
				//
				uni.request({
					url: this.$apiBaseUrl.apiBaseUrl + "/share/resource",
					data: {
						id: this.id,
						msg: this.text,
						// type:this.ty
					}
				}).then(res => {
					res = res[1].data;
					console.log("点击分享", res);
					if (res.code == 200) {
						uni.showToast({
							title: "分享成功",
							duration: 2000
						})
						// 跳转到我的动态页面
						uni.navigateTo({
							url: "/pages/my/child/myupdates"
						})
					}
				})
			},
			// 获取基本信息
			getBaseInfo() {
				if (this.type != "song") {
					uni.request({
						url: this.$apiBaseUrl.apiBaseUrl + "/playlist/detail?id=" + this.id
					}).then(res => {
						res = res[1].data;
						console.log(res);
						if (res.code == 200) {
							this.coverImg = res.playlist.coverImgUrl;
							this.name = res.playlist.name;
						}
					})
				} else {
					// 歌曲的基本信息
					uni.request({
						url: this.$apiBaseUrl.apiBaseUrl + "/song/detail?ids=" + this.id
					}).then(res => {
						res = res[1].data;
						console.log(res);
						this.name = res.songs[0].al.name + " -" + res.songs[0].ar[0].name;
						this.coverImg = res.songs[0].al.picUrl;
					})
				}
			}
		}
	}
</script>

<style>
	.uni-textarea {
		box-shadow: 10px 5px 10px #ebebeb;
		;
		position: relative;
		min-height: 300rpx;
		max-height: 500rpx;
		overflow: scroll;
		width: 100%;
	}

	.input input {
		height: 300rpx;
	}

	.content {
		padding: 0 20rpx;
		margin-top: 20rpx;
		height: 100rpx;
		line-height: 100rpx;
	}

	.cover {
		display: inline-block;
		float: left;
		height: 100rpx;
		width: 100rpx;
		border-radius: 50rpx;
		background-color: lightgray;
	}

	.cover image {
		border-radius: 20rpx;
		height: 100rpx;
		width: 100rpx;
	}

	.name {
		display: inline-block;
		width: 500rpx;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		margin-left: 20rpx;
		font-size: 28rpx;
	}

	.mini-btn {
		position: absolute;
		bottom: 5rpx;
		right: 10rpx;
		border-radius: 30rpx;
	}
</style>
