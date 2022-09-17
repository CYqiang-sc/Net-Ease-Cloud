<template>
	<!-- playbar -->
	<view class="playlist">
		<view class="playbar">
			<view @click="gotosong">
				<image class="avatar" :src="currentplaylist[currentnum].al.picUrl"></image>
				<view class="songdetail">
					<view class="song-name">{{currentplaylist[currentnum].name}}</view>
					<!-- 第一个歌手名 -->
					<view class="song-singer">{{currentplaylist[currentnum].ar[0].name}}</view>
					<!-- </view> -->
				</view>
			</view>
			<image v-if="isplay==0" @click="play" class="play" src="../static/images/play.png"></image>
			<image v-if="isplay==1" @click="pause" class="pause" src="../static/images/pause.png"></image>
			<image class="moreplay" src="../static/images/more_play.png" @click="toshowplaylist()"></image>
		</view>
		<view v-if="isshowplaylist" class="more-action_mask" @click="toshowplaylist()" />
		<view :class="['more-action',isshowplaylist==false ? 'more-action-hide':'more-action-show']">
			<view class="playlist-length">当前播放<text class="length">({{currentplaylist.length}})</text></view>
			<view class="playlist-item" v-for="(item,index) in currentplaylist" :key="index" @click="change(index)">
				<text :class="['action-title',currentnum==index ? 'choosecolor':'']">{{item.name}}</text>
				<text class="playlist-singer">- {{item.ar[0].name}}</text>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getSongDetail,
		getsongurl
	} from "../utils/request.js"
	export default {
		name: "currentplaylist",
		data() {
			return {
				currentplaylist: [],
				isshowplaylist: false,
				currentnum: 0,
				currentsong: [],
				isplay: 0,
				timer: null //定时器
			};
		},
		created() {
			// getApp().globalData.innerAudioContext = uni.createInnerAudioContext();
			this.timer = setInterval(() => {
				if (getApp().globalData.innerAudioContext.currentTime == getApp().globalData.innerAudioContext
					.duration&&(uni.getStorageSync('currentsong').length>1)) {
						
					getApp().globalData.innerAudioContext.destroy();
					console.log('时间到了下一首');
					this.nextPlay();
				}
			}, 100)
		},
		methods: {
			update() {
				// console.log('isMusicPlay',uni.getStorageSync('isMusicPlay'));
				this.isplay = uni.getStorageSync('isMusicPlay');
				console.log(getApp().globalData.innerAudioContext);
				// if (getApp().globalData.innerAudioContext.duration && uni.getStorageSync('isMusicPlay')) {
				// 	this.isplay = true;
				// 	uni.setStorageSync('isMusicPlay', true);
				// } else {
				// 	this.isplay = false;
				// 	// uni.setStorageSync('isMusicPlay', false);
				// }
				this.currentnum = uni.getStorageSync('currentnum');
			},
			gotosong() {
				if (getApp().globalData.innerAudioContext.duration) {
					console.log('已经有歌曲');
					getApp().globalData.innerAudioContext.destroy();
					this.isplay = false;
				}
				uni.navigateTo({
					url: '/pages/song/song?id=' + this.currentsong[this.currentnum]
				})
			},
			getcurrentplay() {
				console.log("测试");
				if (uni.getStorageSync('currentnum')) {
					this.currentnum = uni.getStorageSync('currentnum')
					console.log(this.currentnum);
				}
			},
			getCurrentSongList() {
				const currentsong = uni.getStorageSync('currentsong') || [];
				this.currentsong = currentsong;
				let tempids = '';
				if (currentsong !== undefined && currentsong !== null && currentsong.length > 0) {
					for (let i = 0; i < currentsong.length; i++) {
						const item = currentsong[i];
						tempids += item
						if (i != currentsong.length - 1) {
							tempids += ','
						}
					}
				}
				console.log(tempids);
				getSongDetail(tempids, res => {
					console.log(res);
					if (res.code == 200) {
						this.currentplaylist = res.songs;
					}
				})

			},
			toshowplaylist() {
				this.isshowplaylist = !this.isshowplaylist;
				console.log(this.isshowplaylist);
			},
			change(num) {
				console.log(num);
				this.currentnum = num;
				uni.setStorageSync('currentnum', this.currentnum);
				uni.setStorageSync('currentid', this.currentsong[this.currentnum]);
				this.isplay = true;
				uni.removeStorageSync('isMusicPlay')
				uni.setStorageSync('isMusicPlay', 1);
				setTimeout(() => {
					getsongurl(this.currentplaylist[this.currentnum].id, res => {
						console.log(res);
						if (res.code == 200) {
							getApp().globalData.innerAudioContext.src = res.data[0].url;
							this.play();
							console.log(res.data[0].url);
						}
					})
				}, 500)
				this.toshowplaylist();
			},
			play() {
				console.log(getApp().globalData.innerAudioContext);
				if (getApp().globalData.innerAudioContext.duration) {
					console.log('播放');
					getApp().globalData.innerAudioContext.play();
					this.isplay = true;
				} else {
					getsongurl(this.currentplaylist[uni.getStorageSync('currentnum')].id, res => {
						console.log(res);
						if (res.code == 200) {
							getApp().globalData.innerAudioContext.src = res.data[0].url;
							// console.log(res.data[0].url);
						}
					})
					console.log(getApp().globalData.innerAudioContext.src);
					getApp().globalData.innerAudioContext.onCanplay(() => {
						console.log('开始播放');
						getApp().globalData.innerAudioContext.play();
						this.isplay = true;
						uni.removeStorageSync('isMusicPlay')
						uni.setStorageSync('isMusicPlay', 1);
					});
				}
				uni.removeStorageSync('isMusicPlay')
				uni.setStorageSync('isMusicPlay', 1);
			},
			pause() {
				if (getApp().globalData.innerAudioContext.duration) {
					console.log('暂停');
					getApp().globalData.innerAudioContext.pause();
					this.isplay = false;
				}
				uni.removeStorageSync('isMusicPlay')
				uni.setStorageSync('isMusicPlay', 0);
			},
			nextPlay() {
				console.log('播放下一首');
				getApp().globalData.innerAudioContext.destroy();
				this.time = '';
				let currentid = uni.getStorageSync('currentid');
				let currentnum = uni.getStorageSync('currentnum');
				let currentsong = uni.getStorageSync('currentsong');
				if (currentnum + 1 < currentsong.length) {
					console.log('下一首');
					uni.setStorageSync('currentnum', currentnum + 1);
					currentid = currentsong[currentnum + 1];
					uni.setStorageSync('currentid', currentid);
				} else {
					console.log('第一首');
					currentnum = 0;
					uni.setStorageSync('currentnum', currentnum);
					currentid = currentsong[0];
					uni.setStorageSync('currentid', currentid);
				}
				this.isplay = true;
				this.currentnum = uni.setStorageSync('currentnum', currentnum)
				uni.removeStorageSync('isMusicPlay')
				uni.setStorageSync('isMusicPlay', 1);
				this.change(uni.getStorageSync('currentnum'));
			},
		}
	}
</script>

<style lang="scss">
	.playlist {
		position: fixed;
		bottom: 0;
		// top: 0;
		z-index: 99;
		width: 100%;
	}

	.playbar {
		display: inline-block;
		background-color: rgba(79, 79, 79, 0.6);
		// line-height: 400rpx;
		height: 120rpx;
		width: 100%;
		position: relative;
		vertical-align: middle;
		border-top-left-radius: 20rpx;
		border-top-right-radius: 20rpx;
	}

	.playbar image {
		width: 100rpx;
		height: 100rpx;
		margin-left: 30rpx;
	}

	.song-detail {
		margin-left: 30rpx;
		line-height: 50rpx;
		vertical-align: middle;
		display: block;
	}

	.avatar {
		width: 100rpx;
		height: 100rpx;
		float: left;
		margin-right: 20rpx;
		border-radius: 15rpx;
		bottom: 10rpx;
	}

	.song-name {
		// margin-left: 40rpx;
		font-size: 45rpx;
		line-height: 40rpx;
		width: 360rpx;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		display: block;
		margin-top: 20rpx;
		color: white;
	}

	.song-singer {
		// margin-left: 40rpx;
		margin-top: 10rpx;
		font-size: 28rpx;
		line-height: 40rpx;
		display: block;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		color: white;
	}

	.play {
		position: absolute;
		top: 10rpx;
		right: 130rpx;
	}

	.pause {
		position: absolute;
		top: 10rpx;
		right: 130rpx;
	}

	.moreplay {
		position: absolute;
		top: 10rpx;
		right: 20rpx;
	}

	.more-action_mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		z-index: 10;
		background: rgba(0, 0, 0, 0.4);
		/*设置阴影半透明背景如：background: */
		display: block;
		z-index: 10;
		height: 100%;
	}

	.more-action {
		// padding: 0 20rpx;
		// height: 200rpx;
		background-color: #313131;
		width: 100%;
		// line-height: 240rpx;
		max-height: 520rpx;
		overflow-y: scroll;
		align-items: center;
		position: fixed;
		bottom: 0rpx;
		color: white;
		border-top-left-radius: 30rpx;
		border-top-right-radius: 30rpx;
		z-index: 12;
	}

	.more-action-hide {
		transform: translateY(100%);
		transition-duration: 0.3s;
	}

	.more-action-show {
		transform: translateY(-100rpx);
		transition-duration: 0.3s;
	}

	.playlist-item {
		// position: absolute;
		line-height: 60rpx;
		display: block;
		white-space: nowrap;
		margin: 10rpx 0;
	}

	.action-title {
		display: inline-block;
		margin-left: 70rpx;
		// line-height: 50rpx;
		vertical-align: middle;
		white-space: nowrap;
		width: 300rpx;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.playlist-singer {
		font-size: 28rpx;
		color: #5b5b5b;
		margin-left: 15rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		// line-height: 50rpx;
		vertical-align: middle;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.playlist-length {
		font-size: 45rpx;
		margin-left: 50rpx;
		margin-bottom: 14rpx;
		margin-top: 14rpx;
	}

	.playlist-length .length {
		font-size: 28rpx;
		color: #5b5b5b;
		margin-left: 15rpx;
	}

	.choosecolor {
		color: red;
	}
</style>
