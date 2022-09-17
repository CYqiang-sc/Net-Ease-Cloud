<template>
	<view class="content">
		<!-- 歌曲数目 -->
		<view class="playAll">
			<image src="/static/images/play-all.png"></image>
			<text style="margin-left: 50rpx;">播放全部</text>
			<text style="margin-left: 5rpx; color: #777777;">({{songDetailList.length}})</text>
		</view>
		<!-- 歌曲列表 -->
		<view class="songList">
			<view class="songList-item" v-for="(item,index) in songDetailList" :key="item.id">
				<navigator @click="addCurrentSong(item.id)" :url="'/pages/song/song?id='+item.id+'&name='+item.name"
					hover-class="detail-hover">
					<!-- <view class="num">{{index+1}}</view> -->
					<image class="avatar" :src="item.al.picUrl" lazy-load></image>
					<view class="song-detail">
						<view class="song-name">{{item.name}}</view>
						<view class="song-title">
							<text class="song-detail-vip" v-if="item.fee==1">VIP</text>
							<text class="song-detail-sq">SQ</text>
							<!-- 第一个歌手名 -->
							<view class="song-singer"> {{item.ar[0].name}}</view>
						</view>
					</view>
				</navigator>
				<image src="/static/images/mv.png" class="mv" mode="widthFix" v-if="item.mv != 0"
					@click="gotoPlayMV(item.mv,item)"></image>
				<image src="/static/images/more.png" class="more" mode="widthFix" @click="toShowAction(item.id)">
				</image>
			</view>
		</view>
		<view>
		</view>
		<!-- 歌曲更多操作 -->
		<view>
			<view v-if="isShowAction" class="more-action_mask" @click="toHideAction()" />
			<view :class="['more-action',isShowAction==false ? 'more-action-hide':'more-action-show']">
				<view class="action-item" v-for="(item,index) in singleSong" :key="index">
					<image class="avatar" :src="item.al.picUrl" lazy-load></image>
					<view class="action-title">
						<view class="song-name">{{item.name}}</view>
						<view class="song-title">
							<!-- 第一个歌手名 -->
							<view class="song-singer"> {{item.ar[0].name}}</view>
						</view>
					</view>
				</view>
				<view class="action-item" @click="addSong();toHideAction();">
					<image src="/static/images/next_play.png"></image>
					<text class="action-title">下一首播放</text>
				</view>
				<view class="action-item" @click="toShowCollect()">
					<image src="/static/images/collection.png"></image>
					<text class="action-title">收藏到歌单</text>
				</view>
				<view class="action-item" @click="toComment()">
					<image src="/static/images/comment.png"></image>
					<text class="action-title">评论</text>
				</view>
				<view class="action-item" @click="toShare()">
					<image src="/static/images/share.png"></image>
					<text class="action-title">分享</text>
				</view>
			</view>
			<!-- 收藏到歌单 -->
			<view v-if="isShowCollect" class="more-action_mask" @click="toHideCollect()" />
			<view :class="['more-action',isShowCollect==false ? 'more-action-hide':'more-action-show']">
				<view class="collectlist-title">收藏到歌单</view>
				<view class="collectlist-item">
					<view class="action-item" @click="toShowPlayList();toHideCollect();toHideAction();">
						<image class="avatar" src="/static/images/collection.png" lazy-load></image>
						<view class="action-title">
							<view class="newcollect">新建歌单</view>
						</view>
					</view>
					<view class="action-item" v-for="(item,index) in collectList" :key="index">
						<view @click="goaddSongCollect(item.id)">
							<image class="avatar" :src="item.coverImgUrl" lazy-load></image>
							<view class="action-title">
								<view class="song-name">{{item.name}}</view>
								<view class="song-title">
									<!-- 第一个歌手名 -->
									<view class="song-singer"> {{item.trackCount}}</view>
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>
			<!-- 创建歌单 -->
			<view v-if="isshowplaylist" class="more-action_mask" @click="toShowPlayList()" />
			<view :class="['more-action',isshowplaylist==false ? 'more-action-hide':'more-action-show']">
				<view class="action">
					<text class="quxiao" @click="toShowPlayList()">取消</text>
					<text class="wancheng" @click="goaddPlayList()">完成</text>
				</view>
				<input class="uni-input" ref="input1" adjust-position="true" @input="onKeyInput" placeholder="请输入歌单名称" />
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getSongCollect,
		addSongCollect,
		getSongDetail,
		getLikeList,
		addPlayList
	} from "@/utils/request.js"
	export default {
		name: "typeSongList",
		data() {
			return {
				isShowAction: false, 	//展示跟多操作
				isShowCollect: false, 	//展示收藏歌单
				currentSongId: '', 		//添加播放列表的id
				uid: '',				//用户id
				ids: '',				//歌曲ids
				singleSong: [],
				collectList: [] ,		//收藏的歌单
				songDetailList: [],		//喜欢的歌曲
				isshowplaylist: false, //是否展示创建歌单
				inputValue: '', //input值
			};
		},
		onLoad() {
			//获取用户id
			this.uid = uni.getStorageSync("uid")
			//获取用户喜欢的列表
			getLikeList(this.uid, res => {
				console.log("喜欢", res)
				this.ids = res.ids;
				console.log("id", this.ids)
				//根据id获取到喜欢的歌曲详情
				getSongDetail(this.ids, res => {
					if (res.code == 200) {
						console.log("歌曲", res)
						this.songDetailList = res.songs;
						console.log("喜欢的歌曲", this.songDetailList)
					}
				})
			})
		},
		methods: {
			// 更多操作的展示
			toShowAction(id) {
				this.isShowAction = !this.isShowAction;
				this.currentSongId = id
				getSongDetail(id, res => {
					console.log(res);
					if (res.code == 200) {
						this.singleSong = res.songs;
					}
				})
			},
			toHideAction() {
				this.isShowAction = !this.isShowAction;
			},
			// 添加到播放列表
			addCurrentSong(id) {
				// this.currentSongId=id;
				console.log(id);
				const currentsong = uni.getStorageSync('currentsong') || [];
				let flag = 1; //判断是否在播放列表中
				currentsong.map(item => {
					if (item == id) {
						flag = 0;
					}
				})
				if (flag == 1) {
					uni.showToast({
						title: "添加成功",
						icon: "success",
						mask: true,
					})
					currentsong.unshift(id);
					uni.setStorageSync('currentsong', currentsong);
				}
				uni.setStorageSync('currentnum', 0)
				uni.setStorageSync('currentid', id)

			},
			addSong() {
				const currentsong = uni.getStorageSync('currentsong') || [];
				let flag = 1; //判断是否在播放列表中
				currentsong.map(item => {
					if (item == this.currentSongId) {
						flag = 0;
					}
				})
				if (flag == 1) {
					uni.showToast({
						title: "添加成功",
						icon: "success",
						mask: true,
					})
					currentsong.push(this.currentSongId);
					uni.setStorageSync('currentsong', currentsong);
				}
				if (!uni.getStorageSync('currentnum')) {
					uni.setStorageSync('currentnum', 0)
				}
				if (!uni.getStorageSync('currentid')) {
					uni.setStorageSync('currentid', this.currentSongId)
				}

				// console.log(this.currentSongId);
			},
			// 展示收藏
			toShowCollect() {
				this.isShowCollect = !this.isShowCollect;
				this.isShowAction = !this.isShowAction;
				this.uid = uni.getStorageSync('uid');
				let uid = uni.getStorageSync('uid');
				if (this.collectList.length == 0) {
					getSongCollect(this.uid, res => {
						console.log(res);
						if (res.code == 200) {
							res.playlist.map(item => {
								if (item.creator.userId == uid) {
									this.collectList.push(item);
								}
							})
						}
					})
				}
			},
			toHideCollect() {
				this.isShowCollect = !this.isShowCollect;
				this.isShowAction = !this.isShowAction;
			},
			//跳转到MV
			gotoPlayMV(id, item) {
				console.log("跳转");
				uni.navigateTo({
					url: '/pages/playmv/playmv?mvid=' + id + '&picUrl=' + item.al.picUrl + '&song=' + item.name +
						'&singer=' + item.ar[0].name + '&songid=' + item.id
				})
			},
			//去评论页面
			toComment() {
				console.log("去评论", this.currentSongId);
				uni.navigateTo({
					url: "/pages/comment/comment?id=" + this.currentSongId + "&name=song"
				})
			},
			// 跳转到分享
			toShare() {
				uni.navigateTo({
					url: "/pages/share/share?id=" + this.currentSongId + "&type=song"
				})
			},
			// 展示创建歌单
			toShowPlayList() {
				this.isshowplaylist = !this.isshowplaylist;
			},
			// 获取input值
			onKeyInput: function(event) {
				this.inputValue = event.target.value
			},
			//添加歌单
			goaddPlayList() {
				if (this.inputValue.length > 0) {
					addPlayList(this.inputValue, res => {
						console.log(res)
						if (res.code == 200) {
							uni.showToast({
								title: "添加歌单成功",
								icon: "success",
								mask: true,
							})
							this.toShowPlayList();
						}
					})
				}
			},
			// 收藏歌曲到歌单
			goaddSongCollect(id){
				addSongCollect(id,this.currentSongId, res => {
					console.log(res)
					if (res.code == 200) {
						uni.showToast({
							title: "添加歌曲到歌单成功",
							icon: "success",
							mask: true,
						})
						this.collectList=[];
						this.toHideCollect();
						this.toHideAction();
					}else {
						uni.showToast({
							title: res.body.message,
							icon:"none",
						})
						this.collectList=[];
						this.toHideCollect();
						this.toHideAction();
					}
				})
			},
		}
	}
</script>

<style lang="scss">
	// 歌曲数
	.playAll {
		width: 100%;
		font-size: 28rpx;
		height: 100rpx;
		margin-left: 50rpx;
		margin-top: 25rpx;
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
		right: 60rpx;
	}

	.more {
		width: 80rpx;
		position: absolute;
		top: 10rpx;
		right: -25rpx;
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
	
	// 创建歌单
	.action {
		height: 70rpx;
		line-height: 70rpx;
		display: flex;
		justify-content: space-between;
	}
	
	.quxiao {
		margin-left: 30rpx;
	}
	
	.wancheng {
		margin-right: 30rpx;
	}
	
	.uni-input {
		// color: #313131;
		background-color: transparent;
	}
</style>
