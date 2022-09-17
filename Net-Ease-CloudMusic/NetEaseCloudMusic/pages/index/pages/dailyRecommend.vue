<template>
	<view class="content">
		<view class="cover">
			<img :src="songList[0].al.picUrl" alt="">
			<view class="date">
				<text class="day">{{day}}</text>
				<text class="month">/{{month}}</text>
			</view>
		</view>
		<!-- 播放全部 -->
		<view class="playAll">
			<img src="../../../static/images/play-all.png" />
			<text>播放全部 (30)</text>
		</view>
		<!-- 歌曲列表 -->
		<view class="songList">
			<view class="song-item" v-for="item in songList" :key="item.id">
				<view @click="addcurrentsong(item.id,item)">
					<view class="song-item-cover">
						<img :src="item.al.picUrl" alt="">
					</view>
					<view class="musicName">
						{{item.name}}
					</view>
					<text class="song-detail-vip" v-if="item.fee==1">VIP</text>
					<view class="singer">
						{{item.ar[0].name}}
					</view>
				</view>
				<img src="../../../static/images/mv.png" class="mv" @click="gotoplaymv(item.mv,item)"
					v-if="item.mv != 0" />
				<image src="../../../static/images/more.png" class="more" @click="toshowaction(item.id)" />
			</view>
		</view>
		<!-- 更多 -->
		<view>
			<view v-if="isshowaction" class="more-action_mask" @click="tohideaction()" />
			<view :class="['more-action',isshowaction==false ? 'more-action-hide':'more-action-show']">
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
				<view class="action-item" @click="addSong();tohideaction();">
					<image src="../../../static/images/next_play.png"></image>
					<text class="action-title">下一首播放</text>
				</view>
				<view class="action-item" @click="toshowcollect()">
					<image src="../../../static/images/collection.png"></image>
					<text class="action-title">收藏到歌单</text>
				</view>
				<view class="action-item" @click="toComment()">
					<image src="../../../static/images/comment.png"></image>
					<text class="action-title">评论</text>
				</view>
				<view class="action-item" @click="toShare()">
					<image src="../../../static/images/share.png"></image>
					<text class="action-title">分享</text>
				</view>
			</view>
			<!-- 收藏到歌单 -->
			<view v-if="isshowcollect" class="more-action_mask" @click="tohidecollect()" />
			<view :class="['more-action',isshowcollect==false ? 'more-action-hide':'more-action-show']">
				<view class="collectlist-title">收藏到歌单</view>
				<view class="collectlist-item">
					<view class="action-item" @click="toshowplaylist();tohidecollect();tohideaction();">
						<image class="avatar" src="../../../static/images/collection.png" lazy-load></image>
						<view class="action-title">
							<view class="newcollect">新建歌单</view>
						</view>
					</view>
					<view class="action-item" v-for="(item,index) in collectList" :key="index">
						<view  @click="goaddSongCollect(item.id)">
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
			<view v-if="isshowplaylist" class="more-action_mask" @click="toshowplaylist()" />
			<view :class="['more-action',isshowplaylist==false ? 'more-action-hide':'more-action-show']">
				<view class="action">
					<text class="quxiao" @click="toshowplaylist()">取消</text>
					<text class="wancheng" @click="goaddPlayList()">完成</text>
				</view>
				<input class="uni-input" maxlength="20" ref="input1" adjust-position="true" @input="onKeyInput"
					placeholder="请输入歌单名称" />
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getRecommend,
		getSongDetail,
		getSongCollect,
		addPlayList,
		addSongCollect
	} from "@/utils/request.js"
	export default {
		data() {
			return {
				coverImg: "",
				songList: [],
				day: "",
				month: "",
				isshowaction: false, //展示跟多操作
				isshowcollect: false, //展示收藏歌单
				currentSongid: '', //添加播放列表的id
				uid: '',
				singleSong: [],
				collectList: [], //收藏的歌单
				isshowplaylist: false, //是否展示创建歌单
				inputValue: '', //input值
			}
		},
		onLoad() {
			let userInfo = uni.getStorageSync("userInfo")
			console.log(userInfo);
			if (!userInfo) {
				console.log("没有userInfo");
				uni.showToast({
					title: "请先登录！",
					icon: "error",
					mask: true,
					duration: 2000
				})
				setTimeout(()=>{
					uni.reLaunch({
						url: "/pages/my/my"
					});
				},2000)
				
			}else{
				//获取每日推荐数据
				getRecommend(res => {
					console.log(res, 99);
					if (res.code == 200) {
						this.songList = res.data.dailySongs;
					}else{
						uni.showToast({
							title: "网络错误",
							icon: "error",
							mask: true,
						})
					}
				})
			}
			
			// 获取日期数据
			const day = new Date().getDate();
			const month = new Date().getMonth() + 1;
			this.day = day >= 10 ? day : '0' + day;
			this.month = month >= 10 ? month : '0' + month;

		},
		methods: {
			gotoplaymv(id, item) {
				uni.navigateTo({
					url: '/pages/playmv/playmv?mvid=' + id + '&picUrl=' + item.al.picUrl + '&song=' + item.name +
						'&singer=' + item.ar[0].name + '&songid=' + item.id
				})
			},
			// 更多操作的展示
			toshowaction(id) {
				this.isshowaction = !this.isshowaction;
				this.currentSongid = id
				getSongDetail(id, res => {
					console.log(res);
					if (res.code == 200) {
						this.singleSong = res.songs;
					}
				})
			},
			tohideaction() {
				this.isshowaction = !this.isshowaction;
			},
			// 添加到播放列表
			addcurrentsong(id, item) {
				// this.currentSongid=id;
				uni.navigateTo({
					url: '/pages/song/song?id=' + item.id + '&name=' + item.name
				})
				// console.log(id);
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
					if (item == this.currentSongid) {
						flag = 0;
					}
				})
				if (flag == 1) {
					uni.showToast({
						title: "添加成功",
						icon: "success",
						mask: true,
					})
					currentsong.push(this.currentSongid);
					uni.setStorageSync('currentsong', currentsong);
				}
				if (!uni.getStorageSync('currentnum')) {
					uni.setStorageSync('currentnum', 0)
				}
				if (!uni.getStorageSync('currentid')) {
					uni.setStorageSync('currentid', this.currentSongid)
				}

				// console.log(this.currentSongid);
			},
			// 展示收藏
			toshowcollect() {
				this.isshowcollect = !this.isshowcollect;
				this.isshowaction = !this.isshowaction;
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
			tohidecollect() {
				this.isshowcollect = !this.isshowcollect;
				this.isshowaction = !this.isshowaction;
			},
			//去评论页面
			toComment() {
				console.log("去评论", this.currentSongid);
				uni.navigateTo({
					url: "/pages/comment/comment?id=" + this.currentSongid + "&name=song"
				})
			},
			// 去分享
			toShare() {
				uni.navigateTo({
					url: "/pages/share/share?id=" + this.currentSongid + "&type=song"
				})
			},
			// 展示创建歌单
			toshowplaylist() {
				this.isshowplaylist = !this.isshowplaylist;
			},
			// 获取input值
			onKeyInput: function(event) {
				this.inputValue = event.target.value
			},
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
							this.toshowplaylist();
						}
					})
				}
			},
			// 收藏歌曲到歌单
			goaddSongCollect(id){
				addSongCollect(id,this.currentSongid, res => {
					console.log(res)
					if (res.body.code == 200) {
						uni.showToast({
							title: "添加歌曲到歌单成功",
							icon: "success",
							mask: true,
						})
						this.collectList=[];
						this.tohidecollect();
						this.tohideaction();
					}else {
						uni.showToast({
							title: res.body.message,
							icon:"none",
						})
						this.collectList=[];
						this.tohidecollect();
						this.tohideaction();
					}
				})
			},
		}
	}
</script>

<style>
	@import url("../../../static/css/dailyRecommend .css")
</style>
