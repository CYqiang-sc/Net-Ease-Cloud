<template>
	<view class="box">
		<view class="icon" @tap="collect()">
			<view v-if="!collected">
				<image src="../static/images/action-clollection.png" mode=""></image>
			</view>
			<view v-else>
				<image src="../static/images/choose.png"></image>
			</view>
		</view>
		<view class="icon" @tap="toComment">
			<image src="../static/images/action-comment.png" mode=""></image>
			<!-- <text>{{commentCount}}</text> -->
		</view>
		<view class="icon3" @click="toShare()">
			<image src="../static/images/action-share.png"></image>
		</view>

	</view>
</template>

<script>
	import {loginStatus} from "@/utils/request.js"
	export default {
		name: "songListAction",
		props: {
			id: {
				type: String,
			},
			subscribed: {
				type: Boolean,
			}
		},
		data() {
			return {
				collected: this.subscribed,
			};
		},
		methods: {
			// 去评论页面
			toComment() {
				console.log("去评论", this.$props.id)
				uni.navigateTo({
					url: "/pages/comment/comment?id=" + this.$props.id
				})
				// console.log(JSON.parse(JSON.stringify(this.playlist)));
			},
			// 点击收藏
			collect() {
				if (!this.$props.subscribed) {
					console.log("收藏：",this.$props.subscribed);
					this.collected = true;
					uni.request({
						url: this.$apiBaseUrl.apiBaseUrl + "/playlist/subscribe",
						data: {
							t: "1",
							id: this.$props.id
						}
					}).then(res => {
						res = res[1].data;
						if (res.code == 200) {
							uni.showToast({
								title: "收藏成功",
								duration: 2000
							})
						}else{
							uni.showToast({
								title: res.msg,
								icon: "none",
								duration: 2000
							})
							this.collected = false;
						}
					})
				} else {
					this.collected = false;
					uni.request({
						url: this.$apiBaseUrl.apiBaseUrl + "/playlist/subscribe",
						data: {
							t: "2",
							id: this.$props.id
						}
					}).then(res => {
						res = res[1].data;
						console.log(res);
						if (res.code == 200) {
							console.log(res);
							uni.showToast({
								title: "已取消收藏",
								icon: "none",
								duration: 2000
							})
						}else{
							uni.showToast({
								title: res.msg,
								icon: "none",
								duration: 2000
							})
							this.collected = true;
						}
					})
				}
			},
			
			// 分享
			toShare() {
				uni.navigateTo({
					url: "/pages/share/share?id=" + this.$props.id + "&type=playlist"
				})
			}
		},
		

	}
</script>

<style lang="scss">
	.box {
		// padding: 20rpx 20rpx;
		text-align: center;
		position: absolute;
		margin: 0 30%;
		top: 350rpx;
		float: left;
		width: 50%;
		height: 80rpx;
		line-height: 80rpx;
		background-color: #fff;
		border-radius: 40rpx;
		box-shadow: 0px 3px 8px #b1b1b1;


	}

	.icon {
		text-align: center;
		margin-top: 12rpx;
		display: inline-block;
		width: 30%;
		height: 50rpx;
		border-right: 1px solid darkgray;
	}

	.icon3 {
		text-align: center;
		display: inline-block;
		width: 30%;
		height: 60rpx;
	}

	// .icon:hover{
	// 	background-color:#b1b1b1;
	// }
	.box image {
		margin-top: 0rpx;
		width: 50rpx;
		height: 50rpx;
	}
</style>
