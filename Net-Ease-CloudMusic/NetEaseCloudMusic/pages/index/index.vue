<template>
	<view class="content">
		<!-- 搜索框 -->
		<search class="search"></search>
		<!-- 轮播图 -->
		<view>
			<swiper :indicator-dots="true" :autoplay="true" :interval="3000" :duration="1000">
				<swiper-item v-for="banner in banners" :key="banner.bannerId">
					<img :src="banner.imageUrl" alt="" class="swiper-img">
					<view class="swiper-item"></view>
				</swiper-item>
			</swiper>
		</view>
		<!-- 菜单 -->
		<view class="icon-recommend">
			<view class="icon-item" v-for="(item, index) in iconText" :key="index" @click="toPage(index)">
				<view class="icon">
					<img :src="iconList[index]" alt="">
				</view>
				<view class="text">{{item}}</view>
			</view>
			<view class="date" @click="toPage(0)">{{date}}</view>
		</view>
		<!-- 推荐歌单 -->
		<view class="recommend-list">
			<span class="re-title">推荐歌单</span>
			<span class="re-more" @click="tosongList">更多</span>
			<view class="re-box">
				<scroll-view class="re-songlist" scroll-x enable-flex enhanced scroll-left="120">
					<view class="songlist-item" v-for="item in personalized" :key="item.id" @click="toTopList(item)">
						<img :src="item.picUrl" alt="" lazy-load />
						<view class="text">{{item.name}}</view>
					</view>
				</scroll-view>
			</view>
		</view>
		<!-- 排行榜 -->
		<view class="rank">
			<view class="rank-title">
				排行榜
			</view>
			<view class="rank-more" @click="totopList">
				更多
			</view>
			<view class="rank-list">
				<swiper :indicator-dots="false" :autoplay="false" :interval="3000" :duration="1000">
					<swiper-item v-for="item in topList" :key="item.name" @click="toTopList(item)">
						<view class="rank-item">
							<!-- <view class="title">{{item.name}}</view> -->
							<view class="cover">
								<img :src="item.coverImgUrl" alt="">
							</view>
							<view class="toplist-update">{{item.updateFrequency}}</view>
							<view class="song" v-for="(musicItem, index) in item.tracks" :key="musicItem.id"
								@click="toSongDetail()">
								<text class="count">{{index + 1}}</text>
								<text class="musicName">{{musicItem.first}}</text>
								<text class="singer">-</text>
								<view>
									<text class="singer">{{musicItem.second}}</text>
								</view>

							</view>
						</view>
					</swiper-item>
				</swiper>
			</view>
		</view>
		<currentplaylist v-if="isPlay" class="currentplaylist" ref="currentlist"></currentplaylist>
	</view>
</template>

<script>
	import Search from "../../components/Search.vue";
	import currentplaylist from "../../components/playlist.vue"
	import {
		swipper,
		getPersonalized,
		getTopList,
		getListDetail
	} from "../../utils/request.js"
	export default {
		data() {
			return {
				banners: [], //轮播图
				iconText: ["每日推荐", "歌单", "歌手", "排行榜"],
				iconList: ["../../static/icon/date.png",
					"../../static/icon/playlist.png",
					"../../static/icon/artist.png",
					"../../static/icon/rank.png",
				],
				title: 'Hello',
				date: '01',
				personalized: [], //歌单推荐
				topList: [], //榜单
				songList: [], //榜单歌曲数据
				index: 0,
				isPlay: false,
			}
		},
		onShow() {
			// console.log(uni.getStorageSync('isMusicPlay'),"uni.getStorageSync('isMusicPlay')")
			if (uni.getStorageSync('currentsong')) {
				this.isPlay = true;
				setTimeout(() => {
					this.$refs.currentlist.getCurrentSongList()
				}, 500)
				setTimeout(() => {
					this.$refs.currentlist.getcurrentplay()
				}, 500)
				this.$refs.currentlist.update();
			}
			console.log(this.isPlay);
		},
		onLoad() {
			// 游客登录（测试用）
			// uni.request({
			// 	url: this.$apiBaseUrl.apiBaseUrl + "/register/anonimous"
			// }).then(res => {
			// 	console.log("游客登录:", res);
			// })
			// 获取轮播图数据			
			swipper(res => {
				this.banners = res.banners;
			})
			const today = new Date().getDate();
			this.date = today >= 10 ? today : '0' + today;
			// 获取推荐歌单数据
			const limit = 10;
			getPersonalized(limit, (res) => {
				if (res.code == 200) {
					this.personalized = res.result;
				}
			})

			// 获取榜单数据
			getTopList(res => {
				console.log("排行榜", res);
				if (res.code == 200) {
					this.topList = res.list.slice(0, 4);
				}
			})
			if (!uni.getStorageSync('isMusicPlay')) {
				uni.setStorageSync('isMusicPlay', 0)
			}
		},
		methods: {
			toPage(index) {
				if (index === 0) {
					uni.navigateTo({
						url: "/pages/index/pages/dailyRecommend",
					});
				} else if (index === 1) {
					uni.navigateTo({
						url: "/pages/index/pages/songList",
					});
				} else if (index == 2) {
					uni.navigateTo({
						url: "/pages/index/pages/singerList",
					});
				} else {
					uni.navigateTo({
						url: "/pages/toplist/toplist",
					});

				}
			},
			totopList() {
				uni.navigateTo({
					url: "/pages/toplist/toplist",
				});
			},
			// 去歌单广场
			tosongList() {
				uni.navigateTo({
					url: "/pages/index/pages/songList",
				});
			},
			// 去歌单
			toTopList(item) {
				// /pages/toplistdetail/toplistdetail?id='+item.id+'&name='+item.name
				uni.navigateTo({
					url: "/pages/toplistdetail/toplistdetail?id=" + item.id + "&name=" + item.name,
				});
			}

		},

		components: {
			Search,
			currentplaylist
		}
	}
</script>

<style>
	.search {
		width: 100%;
		height: 100rpx;
	}

	.content {
		display: flex;
		flex-direction: column;
		background-color: rgb(245, 245, 245);
	}

	/* 轮播图样式 */
	.muni-swiper {
		background-color: rgb(224, 224, 224);
		margin: 0 40rpx;
		/* width: 90%; */
		height: 300rpx;
		border-radius: 15rpx;
		overflow: hidden;
	}

	.swiper-img {
		width: 100%;
		height: 100%;
		border-radius: 15rpx;
	}

	/* 菜单样式 */
	.icon-recommend {
		position: relative;
		display: flex;
		background-color: #fff;
	}

	.icon-recommend .icon-item {
		display: flex;
		justify-content: center;
		align-items: center;
		flex-direction: column;
		flex: 1;
		text-align: center;
		margin-top: 30rpx;
		padding-bottom: 30rpx;
		border-bottom: 1px solid rgb(124, 121, 12, 0.3);
	}

	.icon-recommend .icon-item .icon {
		width: 100rpx;
		height: 100rpx;
		border-radius: 50%;
		/* border: 1px solid #d81e06; */
		background-color: #ff000030;
		line-height: 100rpx;
		font-size: 40rpx;
	}

	.icon-recommend .icon-item .icon img {
		width: 80rpx;
		height: 80rpx;
		line-height: 80rpx;
		margin-top: 10rpx;
	}

	.icon-recommend .icon-item .text {
		font-size: 28rpx;
		color: rgb(46, 46, 46);
	}

	.icon-recommend .date {
		position: absolute;
		float: left;
		left: 76rpx;
		top: 72rpx;
		width: 50rpx;
		height: 50rpx;
		font-size: 30rpx;
		font-weight: 600;
		color: rgb(46, 46, 46);
	}

	/* 推荐歌单 */
	.recommend-list {
		display: block;
		height: 400rpx;
		flex: 1;
		/* margin: 20rpx 20rpx; */
		padding: 20rpx 20rpx;
		border-bottom-left-radius: 20rpx;
		border-bottom-right-radius: 20rpx;
		background-color: #fff;
	}

	.re-title {
		display: inline-block;
		font-size: 36rpx;
		font-weight: 700;
		margin-right: 70%;
		margin-bottom: 20rpx;
	}

	.re-more {
		font-size: 30rpx;
		font-weight: normal;
		color: #444444;
	}

	.re-box {
		height: 300rpx;
	}

	.re-songlist {
		white-space: nowrap;
		/* display: flex; */
		height: 300rpx;
	}

	.re-songlist .songlist-item {
		width: 200rpx;
		height: auto;
		display: inline-block;
		margin-right: 20rpx;
	}

	.songlist-item img {
		box-sizing: content-box;
		width: 200rpx;
		height: 200rpx;
		box-shadow: 0rpx -22rpx 5rpx -14rpx #ddd;
		border-radius: 20rpx;
	}

	.songlist-item .text {
		text-overflow: ellipsis;
		white-space: pre-line;
		width: 100%;
		font-size: 26rpx;
		overflow: hidden;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
	}

	/* 排行榜样式 */
	.rank {
		display: block;
		margin-top: 20rpx;
		padding: 20rpx 20rpx;
		border-radius: 20rpx;
		background-color: #fff;
		height: 440rpx;
	}

	.rank-title {
		display: inline-block;
		font-size: 36rpx;
		font-weight: 700;
		margin-right: 76%;
		margin-bottom: 20rpx;
	}

	.rank-more {
		display: inline-block;
		font-size: 30rpx;
		font-weight: normal;
		color: #444444;
	}

	.rank-list {
		width: 100%;
		height: auto;
	}

	.rank-item {
		width: 96%;
		padding: 20rpx;
		position: relative;
	}

	.rank-list .cover img {
		float: left;
		border-radius: 20rpx;
		width: 230rpx;
		height: 230rpx;
	}

	.toplist-update {
		position: absolute;
		padding: 0 8rpx;
		top: 20rpx;
		left: 20rpx;
		font-size: 22rpx;
		line-height: 35rpx;
		color: #f4f4f4;
		border-radius: 30rpx;
		background-color: rgba(233, 233, 233, 0.3);
	}

	.rank-list .song {
		float: right;
		position: relative;
		top: 10rpx;
		right: 20rpx;
		display: flex;
		width: 400rpx;
		height: auto;
		color: #676767;
		margin-bottom: 40rpx;
	}

	.rank-list .song .count {
		font-size: 30rpx;
		margin-right: 20rpx;
	}

	.rank-list .song .musicName {
		margin-right: 30rpx;
		font-size: 28rpx;
		/* max-width: 300rpx; */
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		/*设置对其模式*/
		-webkit-line-clamp: 1;
	}

	.rank-list .song .singer {
		display: -webkit-box;
		-webkit-box-orient: vertical;
		font-size: 24rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		-webkit-line-clamp: 1;
	}

	.currentplaylist {
		position: fixed;
		bottom: 100rpx;
	}
</style>
