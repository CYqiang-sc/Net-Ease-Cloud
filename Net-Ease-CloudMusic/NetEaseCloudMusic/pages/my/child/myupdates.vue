<template>
	<view>
		<view class="user-event" v-for="(item,index) in eventsList" :key="index">
			<view class="left">
				<image class="avatar" :src="item.user.avatarUrl"></image>
			</view>
			<view class="right">
				<text class="nickname">{{item.user.nickname}}</text>
				<!-- <text class="_action" v-if="item">{{item.typeDesc!==null?item.typeDesc:(item.info.commentThread.resourceTitle?item.info.commentThread.resourceTitle:"转发：")}}</text> -->
				
				<view class="detail">
					<view @tap="getdetail(item,item.info.threadId)">
						<view>{{item.json.msg}}</view>
						<text class="link" v-if="item.bottomActivityInfos">
							<a
								:href="item.bottomActivityInfos[0]!==null?item.bottomActivityInfos[0].h5Target:blank">#2{{item.bottomActivityInfos!==null?item.bottomActivityInfos[0].name:null}}</a>
						</text>
						<text class="tibs" v-if="item.bottomActivityInfos">
							<a v-if="item.bottomActivityInfos[1]"
								:href="item.bottomActivityInfos[1]!==null?item.bottomActivityInfos[1].h5Target:blank">#{{item.bottomActivityInfos[1]!==null?item.bottomActivityInfos[1].name:null}}#</a>
						</text>
					</view>
					<image class="pics" v-if="item.pics[0]" :src="item.pics[0].pcSquareUrl"></image>
					<view class="audio" v-if="item.json.song" @click="addcurrentsong(item.json.song.id)">
						<image :src="item.json.song.album.blurPicUrl"></image>
						<view class="song">
							<p class="songname">{{item.json.song.name}}{{item.json.song.album.alias[0]}}</p>
							<text class="songAuther">{{item.json.song.album.artists[0].name}}</text>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getuserevent,
		getTheardId
	} from "../../../utils/request.js"
	export default {
		data() {
			return {
				uid: '',
				eventsList: [],

				detail: {}
			}
		},
		onLoad() {
			this.uid = uni.getStorageSync('uid');
			this.getevent();

		},
		methods: {
			// 获取动态
			getevent() {
				console.log(this.uid);
				getuserevent(this.uid, res => {
					console.log(res);
					if (res.code == 200) {
						this.eventsList = res.events;
						this.eventsList.map(item => {
							item.json = JSON.parse(item.json)
						})
					}
				})
			},
			// 添加到播放列表
			addcurrentsong(id) {
				// this.currentSongid=id;
				console.log(id);
				const currentsong = uni.getStorageSync('currentsong') || [];
				let flag = 1; //判断是否在播放列表中
				currentsong.map(item => {
					if (item == id) {
						flag = 0;
						uni.showToast({
							title: "已在播放列表中",
							icon: "error",
							mask: true,
						})
					}
				})
				if (flag == 1) {
					uni.showToast({
						title: "添加播放列表成功",
						icon: "success",
						mask: true,
					})
					currentsong.unshift(id);
					uni.setStorageSync('currentsong', currentsong);
				}
				uni.setStorageSync('currentnum', 0)
				uni.setStorageSync('currentid', id)

			},
			getdetail(item, theardId) {
				getTheardId(theardId, res => {
					console.log(res)
					this.detail = res
					uni.setStorageSync('item', JSON.stringify(item));
					uni.setStorageSync('detail', JSON.stringify(this.detail));
				})
				console.log('test');
				uni.navigateTo({
					url: "/pages/square/squrdetail"
				});
			}
		}
	}
</script>

<style>
	.user-event {
		width: 100%;
		display: flex;
		flex-direction: row;
		margin: 30rpx 0;
		border-bottom: 1rpx solid rgba(207, 207, 207, 0.6);
		/* background-color: rgba(207, 207, 207, 0.3);
		border-radius: 20rpx; */
	}

	.left {
		display: flex;
		width: 17%;
	}

	.avatar {
		width: 80rpx;
		height: 80rpx;
		margin-left: 3vw;
		border-radius: 50%;
	}

	.right {
		margin-left: 10rpx;
	}

	.nickname {
		display: block;
		font-weight: 800;
		font-size: 36rpx;
		margin-top: 10rpx;
	}

	.detail {
		margin-top: 30rpx;
	}

	.detail .pics {
		width: 400rpx;
		height: 400rpx;
		border-radius: 25rpx;
	}

	.link a {
		color: #648CB7;
		font-size: 18px;
		letter-spacing: 3px;
	}

	.tibs a {
		color: #648CB7;
		font-size: 18px;
		letter-spacing: 3px;
	}

	.audio {
		width: 93%;
		height: 100rpx;
		padding: 20rpx;
		margin: 10px 0;
		background-color: #e9e9e9;
		border-radius: 10px;
	}

	.audio image {
		width: 100rpx;
		height: 100rpx;
		border-radius: 10px;
		float: left;
	}

	.song {
		width: 200px;
		margin-left: 60px;
	}

	.song p {
		font-size: 18px;
		width: 200px;
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
	}

	.song .songAuther {
		font-size: 14px;
		color: #7f7f7f;
	}
</style>
