<template>
	<view class="content">
		<!-- 头部展示 -->
		<view class="header" @click="toDetail">
			<image :src="listInfo.coverImgUrl"></image>
			<text>{{listInfo.name}}</text>
			<image src="../../static/images/more2.png" class="more"></image>
		</view>
		<view class="line">
		</view>
		<!-- 评论区 -->
		<view class="comment">
			<view class="title">
				<text class="t">评论区({{total}})</text>
				<text class="t1" :class="{active: isActive}" @click="getComment()">最新</text>
				<text class="t1" :class="{active: !isActive}" @click="getHotComment()">最热</text>
			</view>
			<view class="comment-item" v-for="(item,index) in comments" :key="index">
				<!-- 头像 -->
				<image :src="item.user.avatarUrl" mode=""></image>
				<text class="name">{{item.user.nickname}}</text>
				<view class="time">
					{{item.timeStr}}
				</view>
				<view class="like">
					{{item.likedCount}}
				</view>
				<view class="zan-icon" @click="like(item)">
					<image src="../../static/images/zan.png" v-if="!item.liked"></image>
					<image src="../../static/images/yizan.png" v-else></image>
				</view>
				<view class="content">
					{{item.content}}
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getTopListDetail,
		getSongDetail,
	} from "../../utils/request.js"
	export default {
		data() {
			return {
				id: "3090014660",
				comments: [],
				hotComments: [],
				isActive: false,
				isActive1: false,
				total: "",
				listInfo: {
					name: "",
					coverImgUrl: ""
				},
				type: "",
			}
		},
		onLoad(e) {
			this.id = e.id;
			this.type = e.name;
			this.getComment();
			this.getListInfo();
		},
		methods: {
			// 获取评论 content (评论内容) likedCount(点赞数) timeStr(时间) user.avatarUrl(用户头像) nickname(昵称)
			getComment() {
				this.isActive = true;
				this.isActive1 = false;
				console.log(this.type);
				if(this.type!="song")
				{
					// 获取歌单评论
					uni.request({
						url: this.$apiBaseUrl.apiBaseUrl + "/comment/playlist?id=" + this.id + "&limit=100"
					}).then(res => {
						console.log(res);
						res = res[1].data;
						if (res.code == 200) {
							this.comments = res.comments;
							this.hotComments = res.hotComments;
							this.total = res.total;
						}
					})
				}else{
					// 获取歌单评论
					uni.request({
						url: this.$apiBaseUrl.apiBaseUrl + "/comment/music?id=" + this.id + "&limit=100"
					}).then(res => {
						res = res[1].data;
						console.log(res);
						if (res.code == 200) {
							this.comments = res.comments;
							this.hotComments = res.hotComments;
							this.total = res.total;
						}
					})
				}
				
			},
			// 获取热评
			getHotComment() {
				this.isActive = false;
				this.isActive1 = true;
				this.comments = this.hotComments;
			},
			// 获取基本信息
			getListInfo() {
				console.log(this.type);
				if (this.type == "song") {
					// 歌曲的基本信息
					uni.request({
						url: this.$apiBaseUrl.apiBaseUrl + "/song/detail?ids="+this.id
					}).then(res => {
						res = res[1].data;
						this.listInfo = {
							name: res.songs[0].al.name,
							coverImgUrl: res.songs[0].al.picUrl,
						}
					})
				} else {
					// 歌单的基本信息
					getTopListDetail(this.id, res => {
						// console.log(res);
						if (res.code == 200) {
							this.listInfo = {
								name: res.playlist.name,
								coverImgUrl: res.playlist.coverImgUrl
							}
						}
					})
				}

			},
			
			toDetail() {
				if(this.type!="song")
				{
					uni.navigateTo({
						url: "/pages/toplistdetail/toplistdetail?id=" + this.id
					})
				}
				else{
					uni.navigateTo({
						url: "/pages/song/song?id=" + this.id
					})
				}
			},
			// 点赞
			like(item){
				if(item.liked==false){
					// 歌单点赞
					if(this.type!="song"){
						this.zan(this.id,item.commentId,1,2);
					}else{
						// 歌曲点赞
						this.zan(this.id,item.commentId,1,0);
					}
					
				}else{
					// 取消赞
					if(this.type!="song"){
						this.zan(this.id,item.commentId,0,2);
					}else{
						this.zan(this.id,item.commentId,0,0);
					}
				}
				
				
			},
			// 点赞
			zan(id,cid,t,type){
				uni.request({
					url: this.$apiBaseUrl.apiBaseUrl+"/comment/like",
					data: {
						id: id,
						cid: cid,
						t: t,
						type: type,
					}
				}).then(res=>{
					if(res[1].data.code==200){
						if(this.isActive){
							this.getComment();
						}else{
							this.getHotComment();
						}
						console.log("点赞 ");
					}
				})
			}
		},

	}
</script>

<style>
	@import url("@/static/css/comment.css");
</style>
