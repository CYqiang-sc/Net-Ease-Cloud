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
				showavatar: true, //??????????????????
				song: [], //????????????
				isstoppic: true, //????????????
				lyric: [], //??????
				scrollTop: -1, //??????????????????
				currentIndex: 0, //????????????
				time: '', //???????????????
				musicurl: '', //??????url
				isplay: false, //????????????
				timer: null, // ????????????????????????
				currentTime: '00:00', // ??????????????????
				width: 0, // ???????????????
				islike: false, //????????????
				uid: '', //??????id
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
			this.getsong(); //??????????????????
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
				console.log('????????????');
				this.showavatar = !this.showavatar
			},
			getsong() {
				getSongDetail(this.id, res => {
					console.log(res);
					if (res.code == 200) {
						this.song = res.songs[0];
						this.time = this.min(res.songs[0].dt) + ':' + this.sec(res.songs[0].dt);
						console.log('?????????' + this.time);
					}
				})
				setTimeout(() => {
					this.geturl(); //???song?????????
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
			// ????????????
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
				let x = parseInt(sec / 60000); //?????????
				let y = parseInt(sec / 1000 - x * 60); //?????????????????????
				if (y < 10) return '0' + y;
				else return y;
			},
			// ????????????
			watchLyric() {
				//????????????
				console.log('????????????');
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
			// ????????????url
			geturl() {
				console.log('????????????url');
				getsongurl(this.id, res => {
					console.log(res);
					if (res.code == 200) {
						this.musicurl = res.data[0].url;
					}
				})
				setTimeout(() => {
					this.setinnerAudioContext(); //???musicurl?????????
				}, 500)

			},
			// ??????innerAudioContext??????
			setinnerAudioContext() {
				// console.log(this.musicurl);
				console.log(this.song.name);
				getApp().globalData.innerAudioContext = uni.createInnerAudioContext(); //????????????
				if (getApp().globalData.innerAudioContext.duration) {
					console.log('???????????????');
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
					console.log('????????????');
					uni.setStorageSync('isMusicPlay', true);
				});
				getApp().globalData.innerAudioContext.onPause(() => {
					console.log('????????????');
					uni.setStorageSync('isMusicPlay', true);
				});
				this.play();
				this.getthelyric();
				this.watchLyric(); //????????????
			},
			// ??????
			play() {
				getApp().globalData.innerAudioContext.play();
				console.log('play???');
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
			// ??????????????????
			changePlayState() {
				if (this.isplay) {
					uni.setStorageSync('isMusicPlay', false);
					this.pause();
				} else {
					uni.setStorageSync('isMusicPlay', true);
					this.play();
				}
			},
			//????????????
			getthelyric() {
				getlyric(this.id, res => {
					console.log(res);
					if (res.code == 200) {
						// ????????????
						const lyric = res.lrc.lyric;
						const list = [];
						let time = '';
						let text = '';
						let istime = true;
						for (let i = 0; i < lyric.length; i++) {
							const temp = lyric[i];
							// i???0??????time??????????????????
							if (temp == '[') {
								if (i != 0) {
									let p = {
										time: '',
										text: ''
									}
									// console.log(time);
									p.time = this.timeChange(time); //?????????????????????time
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
									time += temp //?????????????????????
								} else {
									text += temp
								}
							}
						}
						// ????????????????????????????????????list
						let p = {
							time: '',
							text: ''
						}
						console.log(time);
						p.time = this.timeChange(time); //?????????????????????time
						p.text = text;
						if (p.text.length > 2) {
							list.push(p);
							console.log(list);
						}
						this.lyric = list; //???list???lyric
					}
				})
			},
			previous() {
				console.log('???????????????');
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
				this.showavatar = true; //??????????????????
				this.isstoppic = true; //????????????
				this.isplay = false; //????????????
				setTimeout(() => {
					this.getsong(); //???id?????????
					this.getlikelist();
				}, 500)
			},
			nextPlay() {
				console.log('???????????????');
				getApp().globalData.innerAudioContext.destroy();
				this.time = '';
				let currentid = uni.getStorageSync('currentid');
				let currentnum = uni.getStorageSync('currentnum');
				let currentsong = uni.getStorageSync('currentsong');
				if (currentnum + 1 < currentsong.length) {
					console.log('?????????');
					uni.setStorageSync('currentnum', currentnum + 1);
					currentid = currentsong[currentnum + 1];
					uni.setStorageSync('currentid', currentid);
				} else {
					console.log('?????????');
					currentnum = 0;
					uni.setStorageSync('currentnum', currentnum);
					currentid = currentsong[0];
					uni.setStorageSync('currentid', currentid);
				}
				this.id = currentid;
				this.showavatar = true; //??????????????????
				this.isstoppic = true; //????????????
				this.isplay = false; //????????????
				setTimeout(() => {
					this.getsong(); //???id?????????
					this.getlikelist();
				}, 500)
			},
			//?????????????????????
			changePositionInBar(e) {
				console.log('?????????????????????');
				let left = (e.detail.x - 55) / 275 * 100
				if (left < 0) left = 0
				if (left > 100) left = 100
				this.width = left
				getApp().globalData.innerAudioContext.seek(
					this.song.dt * this.width / 100000
				)
			},
			//???????????????
			changePosition(e) {
				console.log('???????????????');
				clearInterval(this.timer);
				console.log(e);
				let left = (e.touches[0].clientX - 55) / 275 * 100
				if (left < 0) left = 0
				else if (left > 100) left = 100
				this.width = left
			},
			// ???????????????
			stopChange(e) {
				console.log('???????????????');
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

	// ??????
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

	// ????????????
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

	// ????????????
	.showLyric {
		width: 100%;
		height: 70vh;
		// background-color: antiquewhite;
	}

	//????????????
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

	// ??????????????????
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

	// ????????????
	.lyric {
		width: 600rpx;
		height: 60vh;
		margin: 80rpx auto;
		text-align: center;
		position: relative;
	}

	// ????????????
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
	// ?????????
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
