<template>
	<view class="content">
		<view class="bg" :style="{backgroundImage:`url(${song.al.picUrl})`}"></view>
		<view :class="[showavatar==false ? 'hide':'show']">
			<view class="avatar-box">
				<image :src="song.al.picUrl" :class="['avatar-pic',isstoppic==false ? 'pic-stop':'pic-begin']"
					@click="changestate"></image>
				<image class="box-bg" src="../../static/images/song_bg.png"></image>
			</view>
		</view>
		<view :class="['showLyric',showavatar==false ? 'show':'hide']">
			<!-- <scroll-view class="back">
				<image src="../../static/images/back.png"></image>
			</scroll-view> -->
			<scroll-view  @click="changestate" scroll-y="true" :scroll-top="scrollTop" class="lyric">
				<view v-for="(item,index) in lyric" :key="index"
					:class="[currentIndex==index?'lyric-com lyric-active ':'lyric-com']">
					{{item.text}}
				</view>
			</scroll-view>
		</view>
		<view class="song-action">
			<image src="../../static/images/comment.png"></image>

		</view>
		<view class="progress-bar">
			<view class="timeNow">{{currentTime}}</view>
			<view class="bar" @click="changePositionInBar">
				<view class="cur" :style="{width:width+'%'}"></view>
				<view class="pointer" :style="{left: width+'%'}" @touchmove="changePosition" @touchend="stopChange">
				</view>
			</view>
			<view class="timeAll">{{time}}</view>
		</view>
		<view class="bottom-bar">
			<image @click="changelikestate" v-if="!islike" src="../../static/images/like.png"></image>
			<image @click="changelikestate" v-if="islike" src="../../static/images/like_red.png"></image>
			<image @click="previous" class="previous" src="../../static/images/Previous-song.png"></image>
			<image v-if="!isplay" class="play" @click="stoppic(); changePlayState()" src="../../static/images/play.png">
			</image>
			<image v-if="isplay" class="pause" @click="stoppic(); changePlayState()"
				src="../../static/images/pause.png"></image>
			<image @click="nextPlay" class="next" src="../../static/images/next-song.png"></image>
			<image @click="gotocomment" src="../../static/images/comment.png"></image>
		</view>
	</view>
</template>

<script>
	import {
		getSongDetail,
		getlyric,
		getsongurl,
		getLikeList,
		changeLike
	} from "../../utils/request.js"
	export default {
		data() {
			return {
				id: '',
				showavatar: true, //切换歌词展示
				song: [], //歌曲详情
				isstoppic: true, //图片滚动
				lyric: [], //歌词
				scrollTop: -1, //歌词滚动距离
				currentIndex: 0, //歌词索引
				time: '', //歌曲总时长
				musicurl: '', //歌曲url
				isplay: false, //是否播放
				timer: null, // 定时器检测进度条
				currentTime: '00:00', // 当前播放时间
				width: 0, // 进度条比例
				islike: false, //是否喜欢
				uid: '', //用户id
			};
		},
		onShow() {
			if(getApp().globalData.innerAudioContext.duration){
				getApp().globalData.innerAudioContext.destroy();
			}
		},
		onLoad(e) {
			this.id = e.id;
			this.uid = uni.getStorageSync('uid');
			uni.setNavigationBarTitle({
				title: e.name
			})
			this.getsong(); //获取歌曲详情
			if (uni.getStorageSync('isMusicPlay') && uni.getStorageSync('currentid') === e.id) {
				this.isplay = true;
			}
			this.getlikelist();
		},
		methods: {
			stoppic() {
				this.isstoppic = !this.isstoppic
			},
			changestate() {
				console.log('歌词展示');
				this.showavatar = !this.showavatar
			},
			getsong() {
				getSongDetail(this.id, res => {
					console.log(res);
					if (res.code == 200) {
						this.song = res.songs[0];
						this.time = this.min(res.songs[0].dt) + ':' + this.sec(res.songs[0].dt);
						console.log('总时间' + this.time);
					}
				})
				setTimeout(() => {
					this.geturl(); //等song加载好
					uni.setNavigationBarTitle({
						title: this.song.name
					})
				}, 200)
			},
			getlikelist() {
				getLikeList(this.uid, res => {
					console.log(res);
					if (res.code == 200) {
						const likelist = res.ids;
						if (likelist.indexOf(this.id) === -1) {
							this.islike = false;
						}
					}
				})
			},
			changelikestate() {
				changeLike(this.id, !this.islike, res => {
					// console.log(res);
					if (res.code == 200) {
						this.islike = !this.islike;
					}
				})
			},
			// 时间转换
			timeChange(time) {
				let t = time.split(':')
				let min = parseInt(t[0]) //fen
				let sec = parseFloat(t[1])
				// console.log(min * 60 + sec);
				return min * 60 + sec
			},
			min(min) { //1000 ms * 60s
				if (parseInt(min / 60000) < 10)
					return '0' + parseInt(min / 60000);
				else return parseInt(min / 60000)
			},
			sec(sec) {
				let x = parseInt(sec / 60000); //转为分
				let y = parseInt(sec / 1000 - x * 60); //求出剩余多少秒
				if (y < 10) return '0' + y;
				else return y;
			},
			// 监听歌词
			watchLyric() {
				//不停调用
				console.log('监听歌词');
				console.log(getApp().globalData.innerAudioContext.currentTime);
				setInterval(() => {
					for (let i = 0; i < this.lyric.length; i++) {
						let item = this.lyric[i]
						if (Math.abs(getApp().globalData.innerAudioContext.currentTime - item.time) < 0.5) {

							this.$nextTick(() => {
								this.scrollTop = (i - 4) * 39.8 * 2 + 'rpx';
							})
							this.currentIndex = i;
							// console.log(this.scrollTop);
							break
						}
						if (item.time > getApp().globalData.innerAudioContext.currentTime) break;
					}
				}, 200);
			},
			// 获取歌曲url
			geturl() {
				console.log('获取歌曲url');
				getsongurl(this.id, res => {
					console.log(res);
					if (res.code == 200) {
						this.musicurl = res.data[0].url;
					}
				})
				setTimeout(() => {
					this.setinnerAudioContext(); //等musicurl加载好
				}, 500)

			},
			// 设置innerAudioContext属性
			setinnerAudioContext() {
				// console.log(this.musicurl);
				console.log(this.song.name);
				getApp().globalData.innerAudioContext = uni.createInnerAudioContext(); //监听播放
				if (getApp().globalData.innerAudioContext.duration) {
					console.log('已经有歌曲');
					if (getApp().globalData.innerAudioContext.src == this.musicurl) {

						// getApp().globalData.innerAudioContext.play();
					} else {
						getApp().globalData.innerAudioContext.destroy();
						this.time = '';
						getApp().globalData.innerAudioContext.src = this.musicurl;
					}
					this.isplay = true;
				} else {
					getApp().globalData.innerAudioContext.src = this.musicurl;
				}
				// console.log(getApp().globalData.innerAudioContext.src);

				getApp().globalData.innerAudioContext.onCanplay(() => {
					console.log('开始播放');
					uni.setStorageSync('isMusicPlay', true);
				});
				getApp().globalData.innerAudioContext.onPause(() => {
					console.log('暂停事件');
					uni.setStorageSync('isMusicPlay', true);
				});
				this.play();
				this.getthelyric();
				this.watchLyric(); //监听歌词
			},
			// 播放
			play() {
				getApp().globalData.innerAudioContext.play();
				console.log('play歌');
				this.timer = setInterval(() => {
					this.currentTime = this.min(parseInt(getApp().globalData.innerAudioContext.currentTime *
						1000)) + ':' + this
						.sec(parseInt(getApp().globalData.innerAudioContext.currentTime * 1000));
					this.width = getApp().globalData.innerAudioContext.currentTime * 1000 / this.song.dt * 100
					// console.log(this.currentTime);
					if (this.currentTime == this.time) {
						console.log('cw ');
						this.time = '';
						this.nextPlay();
					}

				}, 1000)
				this.isplay = true;
			},
			pause() {
				getApp().globalData.innerAudioContext.pause();
				clearInterval(this.timer)
				this.timer = null;
				this.isplay = false
			},
			// 改变播放状态
			changePlayState() {
				if (this.isplay) {
					uni.setStorageSync('isMusicPlay', false);
					this.pause();
				} else {
					uni.setStorageSync('isMusicPlay', true);
					this.play();
				}
			},
			//获取歌词
			getthelyric() {
				getlyric(this.id, res => {
					console.log(res);
					if (res.code == 200) {
						// 歌词结构
						const lyric = res.lrc.lyric;
						const list = [];
						let time = '';
						let text = '';
						let istime = true;
						for (let i = 0; i < lyric.length; i++) {
							const temp = lyric[i];
							// i为0时，time还没提取出来
							if (temp == '[') {
								if (i != 0) {
									let p = {
										time: '',
										text: ''
									}
									// console.log(time);
									p.time = this.timeChange(time); //每次拼接出来的time
									p.text = text;
									if (p.text.length > 2) {
										list.push(p);
									}
									time = '';
									text = '';
									istime = true
								} else {
									continue
								}

							} else if (temp == ']') {
								istime = false
							} else {
								if (istime) {
									time += temp //判断是否时时间
								} else {
									text += temp
								}
							}
						}
						// 还有最后一句歌词需要加入list
						let p = {
							time: '',
							text: ''
						}
						console.log(time);
						p.time = this.timeChange(time); //每次拼接出来的time
						p.text = text;
						if (p.text.length > 2) {
							list.push(p);
							console.log(list);
						}
						this.lyric = list; //将list给lyric
					}
				})
			},
			previous() {
				console.log('播放上一首');
				getApp().globalData.innerAudioContext.destroy();
				this.time = '';
				let currentid = uni.getStorageSync('currentid');
				let currentnum = uni.getStorageSync('currentnum');
				let currentsong = uni.getStorageSync('currentsong');
				if (currentnum - 1 >= 0) {
					uni.setStorageSync('currentnum', currentnum - 1);
					currentid = currentsong[currentnum - 1];
					uni.setStorageSync('currentid', currentid);
				} else {
					currentnum = currentsong.length - 1;
					uni.setStorageSync('currentnum', currentnum);
					currentid = currentsong[currentnum];
					uni.setStorageSync('currentid', currentsong[currentnum]);
				}
				this.id = currentid;
				this.showavatar = true; //切换歌词展示
				this.isstoppic = true; //图片滚动
				this.isplay = false; //是否播放
				setTimeout(() => {
					this.getsong(); //等id加载好
					this.getlikelist();
				}, 500)
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
				this.id = currentid;
				this.showavatar = true; //切换歌词展示
				this.isstoppic = true; //图片滚动
				this.isplay = false; //是否播放
				setTimeout(() => {
					this.getsong(); //等id加载好
					this.getlikelist();
				}, 500)
			},
			//点击改变进度条
			changePositionInBar(e) {
				console.log('点击改变进度条');
				let left = (e.detail.x - 55) / 275 * 100
				if (left < 0) left = 0
				if (left > 100) left = 100
				this.width = left
				getApp().globalData.innerAudioContext.seek(
					this.song.dt * this.width / 100000
				)
			},
			//拖拽进度条
			changePosition(e) {
				console.log('拖拽进度条');
				clearInterval(this.timer);
				console.log(e);
				let left = (e.touches[0].clientX - 55) / 275 * 100
				if (left < 0) left = 0
				else if (left > 100) left = 100
				this.width = left
			},
			// 释放进度条
			stopChange(e) {
				console.log('释放进度条');
				let left = e.target.offsetLeft
				if (left < 0) left = 0
				if (left > 275) left = 275
				getApp().globalData.innerAudioContext.seek(
					this.song.dt * this.width / 100000
				)
				this.play()
			},
			gotocomment(){
				uni.navigateTo({
					url:"/pages/comment/comment?id=" + this.id + "&name=song"
				})
			}
		}
	}
</script>

<style lang="scss">
	.content {
		position: relative;
		width: 100%;
		height: 100%;
		overflow: hidden;
		// display: flex;
		background-color: rgba(207, 207, 207, 0.5);

	}

	// 背景
	.content .bg {
		width: 100%;
		height: 100%;
		background-repeat: no-repeat;
		-webkit-filter: blur(15px);
		-moz-filter: blur(15px);
		-o-filter: blur(15px);
		-ms-filter: blur(15px);
		filter: blur(15px);
		z-index: -1;
	}

	.show {
		position: absolute;
		top: 0;
		flex: 1;
		opacity: 1;
		transition: 0.5s all ease-in-out;
	}

	.hide {
		position: absolute;
		top: 0;
		flex: 1;
		opacity: 0;
		transition: 0.5s all ease-in-out;
	}

	.avatar-box {
		display: table-cell;
		width: 100vw;
		height: 70vh;
		// background-color: red;
		vertical-align: middle;
		text-align: center;
	}

	.box-bg {
		position: relative;
		width: 400rpx;
		height: 400rpx;
		border: none;
	}

	.avatar-pic {
		position: absolute;
		margin: 75rpx 75rpx;
		z-index: 99;
		width: 250rpx;
		height: 250rpx;
		border: none;
		border-radius: 50%;
		box-shadow: 0px 0px 5px #ccc;
		animation: 0ms;
		-webkit-animation: rotateImg 7s linear infinite;
		animation-fill-mode: forwards;
	}

	.pic-stop {
		animation-play-state: paused;
	}

	.pic-begin {
		animation-play-state: running;
	}

	// 图片旋转
	@keyframes rotateImg {
		0% {
			transform: rotate(0deg);
		}

		100% {
			transform: rotate(360deg);
		}
	}

	@-webkit-keyframes rotateImg {
		0% {
			-webkit-transform: rotate(0deg);
		}

		100% {
			-webkit-transform: rotate(360deg);
		}
	}

	// 展示歌词
	.showLyric {
		width: 100%;
		height: 70vh;
		// background-color: antiquewhite;
	}

	//歌词高亮
	.lyric-com {
		// transition: 1s all ease-in-out;
		color: rgba(207, 207, 207, 1);
		font-size: 30rpx;
	}

	.lyric-active {
		transition: 0.5s all ease-in-out;
		color: white;
		font-size: 38rpx !important;
	}

	// 返回图片滚动
	.back {
		position: absolute;
		width: 100rpx;
		height: 100rpx;
		left: 30rpx;
	}

	.back image {
		margin-top: 30rpx;
		width: 50rpx;
		height: 50rpx;
	}

	// 歌词展示
	.lyric {
		width: 600rpx;
		height: 60vh;
		margin: 80rpx auto;
		text-align: center;
		position: relative;
	}

	// 歌曲操作
	.bottom-bar {
		position: absolute;
		bottom: 4vh;
		width: 100%;
		text-align: center;
		height: 120rpx;
	}

	.bottom-bar image {
		width: 60rpx;
		height: 60rpx;
		z-index: 99;
		margin: 30rpx 3vw;
	}

	.bottom-bar .pause {
		margin: 0 8vw;
		width: 130rpx;
		height: 130rpx;
		line-height: 120rpx;
	}

	.bottom-bar .play {
		margin: 0 8vw;
		width: 130rpx;
		height: 130rpx;
	}

	// .song-action{
	// 	position: absolute;
	// 	bottom: 17vh;
	// 	width: 100%;
	// 	text-align: center;
	// 	height: 120rpx;
	// }
	// .song-action image{
	// 	width: 100rpx;
	// 	height: 100rpx;
	// }
	// 进度条
	.progress-bar {
		position: absolute;
		bottom: 200rpx;
		width: 100%;
		// height: ;
		text-align: center;
		vertical-align: middle;
	}

	.timeNow,
	.timeAll {
		color: #333;
		font-size: 34rpx;
	}

	.timeNow {
		float: left;
		color: #fff;
		margin-left: 18rpx;
		margin-top: -9rpx;
		width: 70rpx;
	}

	.timeAll {
		float: right;
		color: #fff;
		margin-right: 8rpx;
		margin-top: -9rpx;
	}

	.bar {
		position: relative;
		width: 65vw;
		height: 6rpx;
		background-color: rgba(207, 207, 207, 0.9);
		border-radius: 20rpx;
		float: left;
		margin-left: 50rpx;
		margin-top: 10rpx;
		box-sizing: 0 0 5rpx 5rpx #00000020;
	}

	.cur {
		height: 100%;
		background-color: #fff;
		border-radius: 20rpx;
	}

	.pointer {
		width: 8rpx;
		height: 8rpx;
		border-radius: 50%;
		border: 7.5rpx solid #fff;
		background-color: #fff;
		position: absolute;
		top: 0;
		margin-top: -7.5rpx;
		margin-left: -17.5rpx;
	}
</style>
