<template>
	<!--关注与粉丝-->
	<view>
		<!-- header -->
		<view class="header">
			<text :class="{'active': isFollow}" @click="getFollows()">关注</text>
			<text :class="{'active': isFollowed}" @click="getFolloweds()">粉丝</text>
			
		</view>
		<!-- 列表 -->
		<view class="singerList">
			<view class="singer-item" v-for="(item,index) in userList" :key="index">
				<!-- <img src="" alt="" /> -->
				<view class="avatar"  @tap="toSinger(item.id)">
					<image :src="item.avatarUrl" alt="" lazy-load></image>
				</view>
				<text class="name"  @tap="toSinger(item.id)">{{item.nickname}}</text>
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
				isFollow: true,
				isFollowed:false
				
			}
		},
		onLoad(){
			this.uid = uni.getStorageSync('uid')
			this.getFollows()
		},
		
		methods: {
			// 获取关注列表
			getFollows() {
				this.isFollow = true;
				this.isFollowed = false;
				uni.request({
					url: this.$apiBaseUrl.apiBaseUrl + "/user/follows",
					data: {
						uid: this.uid,
					}
				}).then((res) => {
					console.log(res[1].data);
					res = res[1].data;
					if (res.code == 200) {
						this.userList = res.follow;
					}
			
				})
			},
			// 获取粉丝列表
			getFolloweds() {
				this.isFollow = false;
				this.isFollowed = true;
				uni.request({
					url: this.$apiBaseUrl.apiBaseUrl + "/user/followeds",
					data: {
						uid: this.uid,
					}
				}).then((res) => {
					console.log(res[1].data);
					res = res[1].data;
					if (res.code == 200) {
						this.userList = res.followeds;
					}
			
				})
			},
		}
	}
</script>

<style>
.header{
	padding: 10rpx 0 0 0;
	height: 60rpx;
	line-height: 60rpx;
	width: 100%;
	text-align: center;
}
.header text{
	margin: 0 100rpx;
	font-size: 32rpx;
	font-weight: 600;
}
.active{
	font-size: 36rpx;
	color: #D81E06;
	border-bottom: 2px solid #D81E06;
}
.singerList{
	width: 100%;
	padding: 10rpx 10rpx;
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
