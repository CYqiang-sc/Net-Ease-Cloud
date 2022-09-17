<template>
	<view class="content">
		<view class="cover">
			<!-- 背景 -->
			<image :src="artist.cover" alt="" mode="scaleToFill"></image>
		</view>
		<view class="detail">
			<view class="avatar">
				<image :src="artist.cover" alt="" mode="scaleToFill" />
			</view>
			<view class="name">
				{{artist.name}}
			</view>
			<view class="desc">
				{{artist.imageDesc}}
			</view>
			<view  :class="[isFollowed==false ? 'follow':'followed']">
				<text v-if="!isFollowed" @click="follow()">+ 关注</text>
				<text v-else @click="cancel()">√ 已关注</text>
			</view>
		</view>
		<!-- 歌曲列表 -->
		<view class="songList">
			<typeSongList :songDetailList="songList"></typeSongList>
		</view>
		<!-- 底部查看 -->
		<view class="more" @click="getAllsongs()">
			全部演唱>>
		</view>
	</view>
</template>

<script>
	import {
		getHotSong,
		getArtistDetail,
		getArtistFans,
		getFanNum,
		getAllSong
	} from "@/utils/request.js"
	import typeSongList from "../../components/typeSongList.vue"
	export default {
		components: {
			typeSongList
		},
		data() {
			return {
				id: "2116",
				songList: [],
				artist: {
					name: "",
					imageDesc: "",
					cover: ""
				},
				isFollowed:false,
			}
		},
		onLoad(e) {
			this.id = e.id;
			// 获取歌手热门歌曲
			getHotSong(this.id, res => {
				// console.log(res);
				if (res.code == 200) {
					this.songList = res.songs;
				}
			});
			// /artists?id=6452
			getArtistDetail(this.id, res => {
				console.log(res);
				if (res.code == 200) {
					this.artist.cover = res.data.artist.cover;
					this.artist.name = res.data.artist.name;
					this.artist.imageDesc = res.data.identify.imageDesc;
				}
			});
			// 获取粉丝数 接口有问题
			// getFanNum("6452", res=>{
			// 	console.log(res);
			// })
		},
		onShow(){
			let pagearr = getCurrentPages(); //获取应用页面栈
			let currentPage = pagearr[pagearr.length - 1]; //获取当前页面信息
			// console.log('option:', currentPage.options);
			this.isFollowed = currentPage.options.followed=='true' ? true : false;
			// console.log("show",this.isFollowed);
		},
		methods: {
			// 获取歌手全部歌曲
			getAllsongs() {
				uni.navigateTo({
					url:"./allList/allList?id="+this.id
				})
			},
			// 关注
			follow() {
				this.isFollowed=true;
				uni.request({
					url: this.$apiBaseUrl.apiBaseUrl + "/artist/sub",
					data: {
						id: this.id,
						t: "1"
					}
				}).then((res) => {
					console.log(res);
					res = res[1].data;
					if (res.code == 200) {
						uni.showToast({
							title: "关注成功",
							icon: "none",
							mask: true,
						});
						return;
					}
				})
			},
			// 取消关注
			cancel() {
				this.isFollowed = false;
				uni.request({
					url: this.$apiBaseUrl.apiBaseUrl + "/artist/sub",
					data: {
						id: this.id,
						t: "0"
					}
				}).then((res) => {
					res = res[1].data;
					console.log(res);
					if (res.code == 200) {
						uni.showToast({
							title: "已取消关注",
							icon: "none",
							mask: true,
						});
						return;
					}
					else{
						uni.showToast({
							title: res.msg,
							icon: "none",
							mask: true,
						});
					}
				})
			},
		}
	}
</script>

<style>
	@import url("@/static/css/singDetail.css");
</style>
