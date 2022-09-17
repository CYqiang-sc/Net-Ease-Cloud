<template>
	<view>
		<!-- 歌单头部 -->
		<view class="toplist-header">
			<image :src="songDetailList[0].al.picUrl">
				<view class="date">
					<text class="day">{{day}}</text>
					<text class="month">/{{month}}</text>
				</view>
		</view>
		<songListAction :id="id" :subscribed="subscribed"></songListAction>
		<typeSongList :songDetailList="songDetailList"></typeSongList>

	</view>
</template>

<script>
	import {
		getTopListDetail,
		getSongDetail,
	} from "../../utils/request.js"
	import typeSongList from "../../components/typeSongList.vue"
	import songListAction from "../../components/songListAction.vue"
	export default {
		components: {
			typeSongList,
			songListAction
		},
		data() {
			return {
				id: '3090014660',
				ids: '',
				songList: [],
				songDetailList: [], //歌曲详情
				type: 'center',
				singleSong: [],
				day: "",
				month: "",
				commentCount: "",
				shareCount: "",
				subscribed: "",//收藏

			};
		},
		onLoad(e) {
			console.log(e);
			this.id = e.id;
			uni.setNavigationBarTitle({
				title: e.name
			})
			this.getSongList();
			const day = new Date().getDate();
			const month = new Date().getMonth() + 1;
			console.log(day);
			console.log(month);
			this.day = day >= 10 ? day : '0' + day;
			this.month = month >= 10 ? month : '0' + month;
		},
		methods: {
			getSongList() {
				getTopListDetail(this.id, res => {
					console.log("歌单", res.playlist);
					if (res.code == 200) {
						this.songList = res.privileges;
						this.playlist = res.playlist;
						this.commentCount = res.playlist.commentCount;
						this.shareCount = res.playlist.shareCount;
						this.subscribed = res.playlist.subscribed;
						this.getsong();
					}
				})
			},
			getsong() {
				console.log(this.songList);
				if (this.songList !== undefined && this.songList !== null && this.songList.length > 0) {
					for (let i = 0; i < this.songList.length; i++) {
						const item = this.songList[i];
						this.ids += item.id
						if (i != this.songList.length - 1) {
							this.ids += ','
						}
					}
				}
				// console.log("ids");
				// console.log(this.ids);
				getSongDetail(this.ids, res => {
					console.log("歌曲详情",res);
					if (res.code == 200) {
						this.songDetailList = res.songs;
					}
				})
			},


		},
	}
</script>

<style lang="scss">
	// 背景
	.toplist-header {
		background-color: rgba(238, 233, 233, 0.7);
		// height: 350rpx;
		width: 100%;
		position: relative;
		top: 0;
		left: 0;
		height: 400rpx;
	}

	.toplist-header image {
		width: 100%;
		height: 400rpx;
	}

	// 日期
	.date {
		position: absolute;
		bottom: 10rpx;
		left: 50rpx;
		height: 100rpx;
		width: 200rpx;
	}

	.date .day {
		color: white;
		-webkit-text-stroke: 1px #f00;
		font-size: 70rpx;
		font-weight: 700;
	}

	.date .month {
		color: white;
		-webkit-text-stroke: 1px #f00;
	}

	// 歌曲数
	.playAll {
		width: 100%;
		font-size: 28rpx;
		height: 100rpx;
		margin-left: 50rpx;
		margin-top: 45rpx;
		line-height: 100rpx;
		font-weight: bold;
		position: relative;
	}

	.playAll image {
		width: 50rpx;
		height: 50rpx;
		position: absolute;
		left: -25rpx;
		top: 25rpx;
	}

	// 歌曲列表
	.songList {
		width: 700rpx;
		margin: 0rpx auto;
		padding-bottom: 20rpx;
	}

	.songList-item {
		width: 100%;
		height: 110rpx;
		padding-top: 10rpx;
		margin-top: 10rpx;
		position: relative;
		transition: .2s all;
	}

	.avatar {
		width: 100rpx;
		height: 100rpx;
		float: left;
		margin-right: 20rpx;
		border-radius: 15rpx;
	}

	// 歌曲排列
	.num {
		width: 70rpx;
		height: 100rpx;
		float: left;
		margin-right: 20rpx;
		border-radius: 15rpx;
		color: #707070;
		text-align: center;
		line-height: 100rpx;

	}

	.song-detail {
		padding-top: 10rpx;
	}

	.song-name {
		font-size: 28rpx;
		padding-right: 130rpx;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.song-title {
		font-size: 24rpx;
		color: #999;
		line-height: 40rpx;
	}

	.song-detail view {
		height: 40rpx;
		justify-items: center;
	}

	.song-detail-vip {
		border: 1px solid rgb(241, 93, 24);
		border-radius: 10rpx;
		color: rgb(241, 93, 24);
		font-size: 20rpx;
		line-height: 25rpx;
		display: inline-block;
		width: 40rpx;
		text-align: center;
		margin-right: 6rpx;
	}

	.song-detail-sq {
		border: 1px solid rgb(241, 93, 24);
		border-radius: 10rpx;
		color: rgb(241, 93, 24);
		font-size: 20rpx;
		line-height: 25rpx;
		display: inline-block;
		width: 40rpx;
		text-align: center;
		margin-right: 6rpx;
	}

	.mv {
		width: 60rpx;
		height: 60rpx;
		position: absolute;
		top: 20rpx;
		right: 70rpx;
	}

	.more {
		width: 80rpx;
		position: absolute;
		top: 10rpx;
		right: 0;
	}

	.detail-hover {
		background: #ddd;
	}

	.more-action {
		// padding: 0 20rpx;
		// height: 200rpx;
		background-color: #313131;
		width: 100%;
		// line-height: 240rpx;
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
		transform: translateY(0);
		transition-duration: 0.3s;
	}

	.more-action image {
		width: 50rpx;
		height: 50rpx;
		vertical-align: middle;
	}

	// 操作列
	.action-item {
		display: table;
		margin: 40rpx 50rpx;
		line-height: 60rpx;
		width: 100%;
	}

	.action-item .avatar {
		width: 100rpx;
		height: 100rpx;
		float: left;
		margin-right: 20rpx;
		border-radius: 15rpx;
	}

	.action-title {
		// display: table-cell;
		margin-left: 30rpx;
		line-height: 50rpx;
		vertical-align: middle;
	}

	.action-title .song-name {
		font-size: 32rpx;
		white-space: nowrap;
		overflow: hidden;
		width: 500rpx;
		text-overflow: ellipsis;
	}

	// 歌手名
	.song-singer {
		margin-left: 10rpx;
		font-size: 20rpx;
		line-height: 25rpx;
		display: inline;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	// 蒙版
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

	// 收藏到歌单
	.collectlist-title {
		margin-left: 30rpx;
		margin-top: 30rpx;
		font-size: 38rpx;
	}

	.collectlist-item {
		line-height: 60rpx;
		display: block;
		white-space: nowrap;
	}

	.newcollect {
		font-size: 40rpx;
		margin-top: 25rpx;
	}
</style>
