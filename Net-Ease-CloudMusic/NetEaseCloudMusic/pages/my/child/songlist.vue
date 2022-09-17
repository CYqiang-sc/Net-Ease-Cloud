<template>
	<!--歌单详情-->
	<view>
		<view v-if="isUser=='true'">
			<checkbox-group @change="checkboxChange">
				<view class="action-item" v-for="(item,index) in userlist" :key="index">
					<checkbox class="check" :value="item.id" />
					<image class="avatar" :src="item.coverImgUrl" lazy-load></image>
					<view class="action-title">
						<view class="song-name">{{item.name}}</view>
						<view class="song-title">
							<view class="song-singer"> {{item.trackCount}}首</view>
						</view>
					</view>
				</view>
			</checkbox-group>
		</view>
		<view v-if="isUser=='false'">
			<checkbox-group @change="checkboxChange">
				<view class="action-item" v-for="(collectlistitem,collectlistindex) in collectlist"
					:key="collectlistindex">
					<checkbox class="check" :value="collectlistitem.id" />
					<image class="avatar" :src="collectlistitem.coverImgUrl" lazy-load></image>
					<view class="action-title">
						<view class="song-name">{{collectlistitem.name}}</view>
						<view class="song-title">
							<view class="song-singer"> {{collectlistitem.trackCount}}首</view>
						</view>
					</view>
				</view>
			</checkbox-group>
		</view>
		<button @click="dellist">删除</button>
	</view>
</template>

<script>
	import {
		delplaylist,
		getUserPlayList
	} from "@/utils/request.js"
	export default {
		data() {
			return {
				uid: '',
				userlist: [], //用户创建的歌单
				collectlist: [], //用户收藏的歌单
				isUser: true,
				ids: '', //歌单id
			}
		},
		onShow() {
			let pagearr = getCurrentPages(); //获取应用页面栈
			let currentPage = pagearr[pagearr.length - 1]; //获取当前页面信息
			console.log('option:', currentPage.options.isUser) //获取页面传递的信息
			this.isUser = currentPage.options.isUser;
			console.log(this.isUser);
			this.uid = uni.getStorageSync('uid');
			console.log(this.uid);
			this.getuserplaylist();
		},
		methods: {
			getuserplaylist() {
				getUserPlayList(this.uid, res => {
					console.log(res);
					if (res.code == 200) {
						res.playlist.map(item => {
							if (item.creator.userId == this.uid) {
								this.userlist.push(item);
							} else {
								this.collectlist.push(item);
							}
						})
						this.userlist = this.userlist.splice(-(this.userlist.length - 1));
					}
				})
			},
			checkboxChange: function(e) {
				var items = this.items,
					values = e.detail.value;
				console.log(e);
				let ids = '';
				if (values !== undefined && values !== null && values.length > 0) {
					for (let i = 0; i < values.length; i++) {
						const item = values[i];
						ids += item
						if (i != values.length - 1) {
							ids += ','
						}
					}
				}
				this.ids = ids;
				console.log(ids);
			},
			dellist() {
				delplaylist(this.ids, res => {
					console.log(res);
					if (res.code == 200) {
						uni.showToast({
							title: "删除成功！",
							icon: "success",
							mask: true,
						})
						this.userlist = []; //用户创建的歌单
						this.collectlist = []; //用户收藏的歌单
						setTimeout(() => {
							this.getuserplaylist();
						}, 200)
					}
				})
			}
		}
	}
</script>

<style>
	// 操作列
	.action-item {
		display: flex;
		margin: 40rpx 30rpx;
		/* line-height: 60rpx; */
		height: 100rpx;
		width: 100%;
	}

	.check {
		line-height: 100rpx;
	}

	.action-item .avatar {
		width: 100rpx;
		height: 100rpx;
		float: left;
		margin-left: 30rpx;
		margin-right: 10rpx;
		border-radius: 15rpx;
	}

	.action-title {
		// display: table-cell;
		margin-left: 10rpx;
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
</style>
