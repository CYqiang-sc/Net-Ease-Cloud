<template>
	<!--我的-->
	<view class="mine">
		<!--顶部信息-->
		<view class="mine-box" :style="{
				backgroundImage: 'url('+(userInfo.backgroundUrl) ? userInfo.backgroundUrl : baseImage + ')',
				backgroundSize: '100%', borderRadius: '25rpx',}">
			<view class="mine-title">
				<image :src="userInfo.avatarUrl ? userInfo.avatarUrl : userImage"
					style="width: 130rpx; height: 130rpx; border-radius: 50%; border: 2rpx solid #fff;"
					@click="toLogin"></image>
				<view class="mine-nickname">
					<text @click="toLogin">{{userInfo.nickname ? userInfo.nickname : defaultNickname}}</text>
					<view style="display: flex;">
						<image src="/static/images/mine/grade.png"
							style="width: 80rpx; height: 35rpx; padding-right: 10rpx; padding-top: 10rpx;"></image>
						<view class="mine-fans-focus" @click="toFocusFans">{{focus}} 关注</view>
						<view class="mine-fans-focus" @click="toFocusFans">{{fans}} 粉丝</view>
						<view class="mine-level">Lv.{{level}}</view>
						<text v-if="isLogin" class="logout" @click="logout()">退出登录</text>
					</view>
				</view>
			</view>
		</view>
		<!--个人中心导航栏-->
		<view class="navbar-box">
			<view class="navbar-item" hover-class="common-hover" hover-stay-time="50" @click="toRecentPlay">
				<text class="iconfont icon-bofang"></text>
				<text>最近播放</text>
			</view>
			<view class="navbar-item" hover-class="common-hover" hover-stay-time="50" @click="toPersonal">
				<text class="iconfont icon-tubiaozhizuomoban"></text>
				<text>我的动态</text>
			</view>
			<view class="navbar-item" hover-class="common-hover" hover-start-time="50" @click="toFollowSinger">
				<text class="iconfont icon-xihuan-xianxing"></text>
				<text>关注歌手</text>
			</view>
		</view>
		<!--个人中心列表-->
		<view class="person-box">
			<view class="person-item" @click="toFavorite">
				<text class="person-title">我的喜欢</text>
				<image class="item-more" src="/static/images/mine/more.png" @click="toFavorite"></image>
				<view>
					<view v-if="isLogin && isLike">
						<view class="list-box">
							<image class="list-img" :src="userInfo.avatarUrl"></image>
							<view class="list-title">我喜欢的音乐</view>
							<view class="list-number">{{userLikeMusic[0].trackCount}}首</view>
						</view>
					</view>
					<view v-else>
						<view class="list-box">
							<view class="list-img">
								<image src="/static/images/mine/music.png"></image>
							</view>
							<view class="list-title">我喜欢的音乐</view>
							<view class="list-number">{{0}} 首</view>
						</view>
					</view>
				</view>
			</view>
			<!--创建歌单-->
			<view class="person-item">
				<text class="person-title">创建歌单</text>
				<text class="total">({{createList.length}}个)</text>
				<image class="item-more" src="/static/images/mine/more.png" @click="toCreateSongList"></image>
				<image class="item-more" src="/static/images/mine/create.png" @click="toShowPlayList()"></image>
				<view>
					<view v-if="isLogin && isCreate">
						<view class="list-box" v-for="item in createList" :key="item.id" @click="toSongDetail(item)">
							<view class="list-img-recom">
								<image :src="item.coverImgUrl"></image>
							</view>
							<view class="list-title">{{item.name}}</view>
							<view class="list-number">{{item.trackCount}} 首</view>
							<!-- <view class="list-detail">
								<image src="/static/images/mine/detail.png"></image>
							</view> -->
						</view>
					</view>
					<view v-else>
						<view class="preview-default">
							<text class="title-default">暂无创建的歌单</text>
						</view>
					</view>
				</view>
			</view>
			<!--收藏歌单-->
			<view class="person-item">
				<text class="person-title">收藏歌单</text>
				<text class="total">({{recommendList.length}}个)</text>
				<image class="item-more" src="/static/images/mine/more.png" @click="toSongList"></image>
				<view>
					<view v-if="isLogin && isRecommend">
						<view class="list-box" v-for="item in recommendList" :key="item.id" @click="toSongDetail(item)">
							<view class="list-img-recom">
								<image :src="item.coverImgUrl"></image>
							</view>
							<view class="list-title">{{item.name}}</view>
							<view class="list-number">{{item.trackCount}} 首</view>
							<!-- <view class="list-detail">
								<image src="/static/images/mine/detail.png"></image>
							</view> -->
						</view>
					</view>
					<view v-else>
						<view class="preview-default">
							<text class="title-default">暂无收藏的歌单</text>
						</view>
					</view>
				</view>
			</view>
		</view>
		<!--显示底部创建歌单弹窗-->
		<view v-if="isShowPlayList" class="more-action_mask" @click="toShowPlayList()" />
		<view :class="['more-action',isShowPlayList==false ? 'more-action-hide':'more-action-show']">
			<view class="action">
				<text class="quxiao" @click="toShowPlayList()">取消</text>
				<text class="wancheng" @click="goaddPlayList()">完成</text>
			</view>
			<input class="uni-input" ref="input1" adjust-position="true" @input="onKeyInput" placeholder="请输入歌单名称" />
		</view>
		<!--显示底部详情歌单-->
	</view>
</template>

<script>
	import {
		login,
		getLevel,
		getFansCount,
		getFocusCount,
		getSongCollect,
		addPlayList
	} from '@/utils/request.js'
	export default {
		name: "Mine",
		data() {
			return {
				baseImage: '/static/images/mine/base.png',
				userImage: '/static/images/mine/user.png',
				defaultNickname: '请登录',
				uid: '', //用户id
				userInfo: {}, //用户信息
				level: 0, //等级
				fans: 0, //粉丝
				focus: 0, //关注
				isLogin: false, //是否登录
				isLike: false, //是否有喜欢
				isCreate: false, //是否有创建
				isRecommend: false, //是否有收藏
				userLikeMusic: [], //用户歌单
				createList: [], //创建歌单
				recommendList: [], //收藏歌单
				isShowPlayList: false, //是否展示创建歌单
				inputValue: '', //input值
			};
		},
		onLoad() {
			//获取本地登录缓存
			this.getUserInfo()
			// 获取用户id
			this.uid = uni.getStorageSync("uid")
			//获取用户等级
			getLevel(this.uid, res => {
				if (res.code == 200) {
					console.log("等级", res)
					this.level = res.data.level;
				}
			})
			console.log("登录", this.isLogin)
			//获取用户的粉丝数
			getFansCount(this.uid, res => {
				if (res.code == 200) {
					console.log("粉丝", res)
					this.fans = res.followeds.length;
				}
			})
			//获取用户的关注数
			getFocusCount(this.uid, res => {
				if (res.code == 200) {
					console.log("关注", res)
					this.focus = res.follow.length;
				}
			})
			//获取用户的歌单
			getSongCollect(this.uid, res => {
				if (res.code == 200) {
					console.log("歌单", res)
					this.userLikeMusic = res.playlist.splice(0, 1);
					this.isLike = !this.isLike;
					console.log("喜欢", this.isLike)
					console.log("喜欢的音乐", this.userLikeMusic)
					res.playlist.map(item => {
						if (item.creator.userId == this.uid) {
							this.createList.push(item);
						} else {
							this.recommendList.push(item);
						}
					})
					if (this.createList.length > 0) {
						// console.log("创建的数量",this.createList.length)
						// console.log("创建的歌单",this.createList)
						this.isCreate = !this.isCreate;
						// console.log("创建",this.isCreate)
					}
					if (this.recommendList.length > 0) {
						// console.log("收藏的数量",this.recommendList.length)
						// console.log("收藏的歌单",this.recommendList)
						this.isRecommend = !this.isRecommend;
						console.log("收藏歌单数", this.recommendList.length)
						// console.log("收藏",this.isRecommend)
					}
				}
			})
		},
		methods: {
			//获取本地缓存数据
			getUserInfo() {
				let userInfo = uni.getStorageSync("userInfo");
				if (userInfo) {
					this.userInfo = JSON.parse(userInfo);
					this.isLogin = !this.isLogin;
				}
			},
			//跳转登录界面
			toLogin() {
				//第一次未登录状态点击跳转到登录
				if (!uni.getStorageSync("userInfo")) {
					// 微信授权登录
					//showModal用于触发uni.getUserProfile弹窗
					uni.showModal({
						title: '授权登录',
						content: '是否授权登录微信小程序？',
						success: () => {
							uni.getUserProfile({
								desc: '登录后可同步数据',
								lang: 'zh_CN',
								success: (infoRes) => {
									console.log('用户信息-----------', infoRes)
									//获取code
									uni.login({
										provider: 'weixin',
										success: (loginRes) => {
											console.log('获取code-----------', loginRes)
											uni.setStorageSync('code', loginRes.code);
											//调用接口传递参数
											let parames = {
												code: loginRes.code,
												encryptedData: infoRes
													.encryptedData,
												iv: infoRes.iv,
												signature: infoRes.signature,
												rawData: infoRes.rawData
											};
											// 获取openid
											uni.request({
												url: "https://api.weixin.qq.com/sns/jscode2session",
												data: {
													appid: "wx90d3328edfe8f704",
													secret: "7aa7c0374dc3af309e5e8629d2ff531c",
													js_code: loginRes.code,
													grant_type: "authorization_code"
												}
											}).then(res => {
												console.log("openid:", res);
												uni.setStorageSync('openid',
													res[1].data.openid);
											})
											console.log('将参数传给后端-----------', parames)
											// 一键登录
											setTimeout(() => {
												uni.request({
													url: "http://162.14.109.33:8081/login/appId",
													data: {
														appId: uni.getStorageSync('openid'),
													},
												}).then(res => {
													res = res[1].data;
													console.log("一键登录返回",res)
													// 未绑定wx 去登录页面
													if (res.code == 0) {
														if(res.msg=='请先绑定您的账号'){
															uni.showToast({
																title: res.msg,
																icon: 'none',
															})
															uni.navigateTo({
																url: "/pages/login/login"
															})
														}else{
															console.log(res.msg);
															uni.showToast({
																title: res.msg,
																icon: 'error',
																duration: 2000
															})
															setTimeout(()=>{
																uni.reLaunch({
																	url: '/pages/my/my'
																})
															},2000)
															
														}
														
													} else {
														// 已绑定wx 登录存信息
														uni.showToast({
															icon: 'none',
															title: '授权成功'
														})
														// 获取返回的用户名密码
														let phone = res.data.phone;
														let pwd = res.data.password;
														// 调网易云登录
														login(phone, pwd, res => {
															console.log("loginmsg:", res)
															if (res.code === 200) {
																uni.showToast({
																	title: "登录成功",
																	icon: "success",
																	mask: true,
																});
																uni.setStorageSync('cookie', res.cookie);
																uni.setStorageSync('uid', res.account.id);
																uni.setStorageSync("userInfo", JSON.stringify(res.profile));
																setTimeout(() => {
																	uni.reLaunch({
																		url: "/pages/my/my"
																	});
																}, 1000);
															} else {
																uni.showToast({
																	title: res.msg,
																	icon: "none",
																	mask: true,
																});
																return;
															}
														})
														setTimeout(() => {
																uni.reLaunch({
																	url: "/pages/my/my"
																});
															}, 1000
														);
													}
												})
											}, 2000)
										},
										fail: function(err) {
											uni.showToast({
												icon: 'none',
												title: '授权失败'
											})
										}
									});
								},
								fail: function(err) {
									uni.showToast({
										icon: 'none',
										title: '授权失败'
									})
								}
							});
						}
					})
				} else {
					uni.showToast({
						title: "当前已登录",
						duration: 500
					});
				}
			},
			//退出登录跳转至首页
			logout() {
				uni.showModal({
					title: '退出登录',
					content: '确定要退出登录吗？',
					success: (res => {
						if (res.confirm) {
							console.log("用户点击确认")
							uni.request({
								url: this.$apiBaseUrl.apiBaseUrl + "/logout"
							}).then(res => {
								//退出成功后跳转至首页并延时加载
								setTimeout(() => {
									uni.reLaunch({
										url: '/pages/index/index'
									});
								})
							})
							//清除本地所有的缓存
							uni.clearStorageSync();
						} else if (res.cancel) {
							console.log("用户点击取消")
						}
					})
				})
			},
			//跳转粉丝关注界面
			toFocusFans() {
				if (uni.getStorageSync("userInfo")) {
					uni.navigateTo({
						url: '/pages/my/child/focusfans'
					});
				} else {
					uni.showToast({
						title: "请先登录",
						duration: 500
					});
					setTimeout(function() {
						uni.navigateTo({
							url: '/pages/login/login'
						});
					}, 500)
				}
			},
			//跳转最近播放界面
			toRecentPlay() {
				if (uni.getStorageSync("userInfo")) {
					uni.navigateTo({
						url: '/pages/my/child/recentplay'
					});
				} else {
					uni.showToast({
						title: "请先登录",
						duration: 500
					});
					setTimeout(function() {
						uni.navigateTo({
							url: '/pages/login/login'
						});
					}, 500)
				}
			},
			//跳转个人动态页面
			toPersonal() {
				if (uni.getStorageSync("userInfo")) {
					uni.navigateTo({
						url: '/pages/my/child/myupdates'
					});
				} else {
					uni.showToast({
						title: "请先登录",
						duration: 500
					});
					setTimeout(function() {
						uni.navigateTo({
							url: '/pages/login/login'
						});
					}, 500)
				}
			},
			//跳转关注歌手详情页面
			toFollowSinger() {
				if (uni.getStorageSync("userInfo")) {
					uni.navigateTo({
						url: '/pages/my/child/followsinger'
					})
				} else {
					uni.showToast({
						title: "请先登录",
						duration: 500
					});
					setTimeout(function() {
						uni.navigateTo({
							url: '/pages/login/login'
						});
					}, 500)
				}
			},
			//跳转我喜欢的音乐列表页面
			toFavorite() {
				if (uni.getStorageSync("userInfo")) {
					uni.navigateTo({
						url: '/pages/my/child/favorite'
					});
				} else {
					uni.showToast({
						title: "请先登录",
						duration: 500
					});
					setTimeout(function() {
						uni.navigateTo({
							url: '/pages/login/login'
						});
					}, 500)
				}
			},
			//跳转收藏的歌单列表
			toSongList() {
				if (uni.getStorageSync("userInfo")) {
					uni.navigateTo({
						url: '/pages/my/child/songlist?isUser=' + false
					});
				} else {
					uni.showToast({
						title: "请先登录",
						duration: 500
					});
					setTimeout(function() {
						uni.navigateTo({
							url: '/pages/login/login'
						});
					}, 500)
				}
			},
			//跳转到创建的歌单列表
			toCreateSongList() {
				if (uni.getStorageSync("userInfo")) {
					uni.navigateTo({
						url: '/pages/my/child/songlist?isUser=' + true
					});
				} else {
					uni.showToast({
						title: "请先登录",
						duration: 500
					});
					setTimeout(function() {
						uni.navigateTo({
							url: '/pages/login/login'
						});
					}, 500)
				}
			},
			//跳转歌单歌曲详情列表页面
			toSongDetail(item) {
				if (uni.getStorageSync("userInfo")) {
					uni.navigateTo({
						url: '/pages/toplistdetail/toplistdetail?id=' + item.id + "&name=" + item.name
					});
				} else {
					uni.showToast({
						title: "请先登录",
						duration: 500
					});
					setTimeout(function() {
						uni.navigateTo({
							url: '/pages/login/login'
						});
					}, 500)
				}
			},
			// 展示创建歌单
			toShowPlayList() {
				this.isShowPlayList = !this.isShowPlayList;
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
			}
		}
	}
</script>

<style lang="scss">
	.mine {
		display: flex;
		flex-direction: column;
		margin-top: 30rpx;
		background-color: #f9f9f9;
	}

	/*我的信息*/
	.mine-box {
		margin: 40rpx 10rpx 40rpx 10rpx;
		position: relative;
		z-index: 1;
		display: flex;
		flex-direction: column;
		background: #fff;
	}

	.mine-title {
		display: flex;
		align-items: center;
		margin: 25rpx 25rpx 25rpx 25rpx;
	}

	.mine-nickname {
		display: bloack;
		white-space: nowrap;
		text-overflow: ellipsis;
		overflow: hidden;
		text-align: left;
		flex-direction: column;
		width: 80%;
		margin-left: 20rpx;
		font-size: 47rpx;
	}

	.logout {
		display: flex;
		justify-content: center;
		align-items: center;
		width: 130rpx;
		align-items: center;
		color: #777777;
		font-size: 25rpx;
		margin-left: 45rpx;
		margin-top: 10rpx;
		border-radius: 20rpx;
		border: 3rpx solid #d4d4d4;
	}

	.mine-level {
		border-radius: 10rpx;
		font-size: 30rpx;
		color: #555555;
		margin: 10rpx 10rpx 0 10rpx;
	}

	.mine-fans-focus {
		white-space: nowrap;
		border-radius: 10rpx;
		font-size: 30rpx;
		color: #555555;
		margin: 10rpx 10rpx 0 0;
	}

	/*导航*/
	.navbar-box {
		display: flex;
		height: 100rpx;
		background: #fff;
		border-radius: 25rpx;
		padding: 15rpx 10rpx 35rpx 10rpx;
		margin: 0 10rpx 40rpx 10rpx;
	}

	.navbar-box .navbar-item {
		width: 34%;
		box-sizing: border-box;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.navbar-box .navbar-item .iconfont {
		font-size: 50rpx;
		color: #d43c33;
		line-height: 70rpx;
	}

	.navbar-box .navbar-item text:last-child {
		font-size: 30rpx;
		color: #333333;
	}

	/*个人中心列表*/
	.person-box {
		background: #fff;
		margin: 0 10rpx 40rpx 10rpx;
		border-radius: 25rpx;
	}

	.person-item {
		border-bottom: 1rpx solid #eee;
	}

	.person-item .person-title {
		display: inline-block;
		height: 60rpx;
		font-size: 38rpx;
		color: #333333;
		margin: 15rpx 5rpx 10rpx 30rpx;
		line-height: 60rpx;
	}

	.person-item .total {
		position: absolute;
		font-size: 30rpx;
		color: #777777;
		margin-top: 22rpx;
	}

	.person-item .item-more {
		float: right;
		height: 40rpx;
		width: 40rpx;
		margin-top: 27rpx;
		margin-right: 30rpx;
	}

	/*默认预览*/
	.person-item .preview-default {
		height: 90rpx;
		padding-top: 10rpx;
		margin-bottom: 10rpx;
	}

	.title-default {
		font-size: 30rpx;
		color: #555555;
		padding-left: 38%;
	}

	/*列表通用*/
	.list-box {
		display: flex;
		border-radius: 25rpx;
		position: relative;
	}

	.list-box .list-img {
		height: 130rpx;
		width: 130rpx;
		background-color: #eee;
		margin: 0 10rpx 20rpx 34rpx;
		border-radius: 25rpx;
		position: relative;
	}

	/*默认音乐图标*/
	.list-box .list-img image {
		height: 70rpx;
		width: 70rpx;
		margin: 30rpx 10rpx 0 25rpx;
	}

	/*收藏与创建图片样式*/
	.list-box .list-img-recom image {
		height: 130rpx;
		width: 130rpx;
		background-color: #eee;
		margin: 0 10rpx 20rpx 34rpx;
		border-radius: 25rpx;
	}

	/*歌单名字*/
	.list-box .list-title {
		display: inline-block;
		position: absolute;
		white-space: nowrap;
		text-overflow: ellipsis;
		overflow: hidden;
		width: 450rpx;
		margin: 25rpx 0 10rpx 195rpx;
		font-size: 30rpx;
	}

	/*歌单歌曲数量*/
	.list-box .list-number {
		display: inline-block;
		position: absolute;
		margin: 67rpx 0 10rpx 195rpx;
		font-size: 20rpx;
	}

	/*歌单列表详情*/
	.list-box .list-detail image {
		display: inline-block;
		position: absolute;
		height: 50rpx;
		width: 50rpx;
		margin-left: 487rpx;
		margin-top: 39rpx;
	}

	/*创建歌单*/
	.more-action_mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		z-index: 10;
		background: rgba(0, 0, 0, 0.4);
		display: block;
		z-index: 10;
		height: 100%;
	}

	.more-action {
		background-color: #313131;
		width: 100%;
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

	/*弹出后行为*/
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
		background-color: transparent;
	}
</style>
