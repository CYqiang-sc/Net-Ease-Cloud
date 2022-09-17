<template>
	<view>
		<view class="singerList">
			<view class="singer-item" v-for="(item,index) in userList" :key="index" @click="toSinger(item.id)">
				<!-- <img src="" alt="" /> -->
				<view class="avatar">
					<image :src="item.picUrl" alt="" lazy-load></image>
				</view>
				<text class="name">{{item.name}}</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				userList: [],
				uid:'',
			}
		},
		onLoad(){
			this.uid = uni.getStorageSync('uid')
			this.getFollows()
		},
		methods: {
			// 获取关注列表
			getFollows() {
				uni.request({
					url: this.$apiBaseUrl.apiBaseUrl + "/artist/sublist",
				}).then((res) => {
					console.log(res[1].data);
					res = res[1].data;
					if (res.code == 200) {
						this.userList = res.data;
					}
			
				})
			},
			toSinger(id) {
				console.log("歌手id", id);
				uni.navigateTo({
					url: "/pages/singerDetail/singerDetail?id=" + id
				})
			}
			
		}
	}
</script>

<style>

.singerList{
	width: 100%;
	padding: 10rpx 20rpx;
	background-color: #fff;
}
.singerList .singer-item{
	display: block;
	height: 100rpx;
	line-height: 100rpx;
	margin-bottom: 20rpx;
	/* border-bottom: 1px solid rgb(0, 0, 0, 0.3); */
}
.singer-item .avatar image{
	/* display: inline-block; */
	float: left;
	height: 100rpx;
	width: 100rpx;
	border-radius: 50rpx;
}
.singer-item .name{
	font-size: 32rpx;
	text-align: center;
	margin-left: 20rpx;
}
</style>
