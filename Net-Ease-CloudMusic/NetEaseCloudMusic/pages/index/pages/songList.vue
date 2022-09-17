<template>
	<view class="content">
		<!-- 导航区域 -->
		<scroll-view scroll-x class="navScroll" enable-flex scroll-with-animation>
			<view class="navItem" v-for="(item,index) in hostListType" :key="index" @tap="handleSelected(index)">
				<view :class="{'active': item.selected}" class="navContent">
					{{item.name}}
					<view class="line" v-if="item.selected">
					</view>
				</view>
			</view>
			
		</scroll-view>
		<!-- 选择更多标签 -->
		<view class="more">
			》
		</view>
		<!-- <view class="select-down" v-show="showMenu">
				<text class="select-down-item" v-for="(item,index) in hostListType" :key='index' @click="item_click(item)">{{item.name}}</text>
		</view> -->
		
		<!-- 歌单区域 -->
		<view class="songList">
			<!-- tag标签歌单 -->
			<view class="title" v-if="hostListType[0].selected">
				今日达人推荐
			</view>
			<view class="list-item" v-for="(item,index) in hostList" :key="index">
				<navigator :url="'/pages/toplistdetail/toplistdetail?id='+item.id+'&name='+item.name+'&picurl='+item.coverImgUrl" hover-class="navigator-hover">
					<image :src="item.coverImgUrl" style="width: 200rpx; height: 200rpx; border-radius: 20rpx;"></image>
				</navigator>
				<view class="playCount">
				<img src="../../../static/icon/playCount.png" alt="" class="play">
				<text> {{item.playCount}}</text>
				</view>
				<text class="list-name">{{item.name}}</text>
			</view>
		</view>
		<!-- 精品歌单 -->
		<view class="highLevel" v-if="hostListType[0].selected">
			<view class="title">
				精品歌单
			</view>
			<scroll-view class="highLevel-songlist" scroll-x enable-flex enhanced scroll-left="120">
				<view class="songlist-item" v-for="item in highLevelList" :key="item.id">
					<!-- <img :src="item.picUrl" alt="" lazy-load /> -->
					<navigator :url="'/pages/toplistdetail/toplistdetail?id='+item.id+'&name='+item.name+'&picurl='+item.coverImgUrl" hover-class="navigator-hover">
						<image :src="item.coverImgUrl" style="width: 200rpx; height: 200rpx; border-radius: 20rpx;" lazy-load></image>
					</navigator>
					<view class="txt">{{item.name}}</view>
				</view>
				</scroll-view>
		</view>
		<!-- 官方推荐歌单 -->
		<view class="songList">
			<!-- tag标签歌单 -->
			<view class="title" v-if="hostListType[0].selected">
				官方推荐
			</view>
			<view class="list-item" v-for="(item,index) in recommendLsit" :key="index">
				<navigator :url="'/pages/toplistdetail/toplistdetail?id='+item.id+'&name='+item.name+'&picurl='+item.coverImgUrl" hover-class="navigator-hover">
					<image :src="item.picUrl" style="width: 200rpx; height: 200rpx; border-radius: 20rpx;"></image>
				</navigator>
				<view class="playCount">
				<img src="../../../static/icon/playCount.png" alt="" class="play">
				<text> {{item.playCount}}</text>
				</view>
				<text class="list-name">{{item.name}}</text>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getlistType,
		getHotListType,
		gethotList
	} from "@/utils/request.js"
	export default {
		data() {
			return {
				hostListType: [],//歌单分类
				hostList:[],//热门歌单
				highLevelList:[],//精品歌单
				recommendLsit:[],//官方推荐歌单
				showMenu:true,
			}
		},
		onLoad() {
			//所有歌单分类
			getlistType(res => {
				// console.log(res);
			})
			// 热门歌单分类
			getHotListType(res => {
				// console.log(res);
				if (res.code == 200) {
					res.tags.forEach(item => {
						item.selected = false;
					})
					
					this.hostListType = res.tags;
					var obj={
						name:"推荐",
						selected: false
					};
					this.hostListType.unshift(obj);
					this.hostListType[0].selected = true;
				}
			})
			this.initList();
			
			
			// gethotList(tag,res=>{
			// 	console.log(res);
			// 	//playCount 播放量  name 标题   coverImgUrl 封面
			// 	if(res.code==200){
			// 		this.hostList = res.playlists;
			// 	}
			// })
		},
		methods: {
			// 选择歌单分类
			handleSelected(index) {
				this.hostListType[index].selected = true;
				if(index==0){
					this.initList();
				}else{
					const tag = this.hostListType[index].name;
					gethotList(tag,res=>{
						// console.log(res);
						this.hostList = res.playlists;
					})
				}
				for (let i = 0; i < this.hostListType.length; i++) {
					if (i != index) {
						this.hostListType[i].selected = false
					}
				}
			},
			initList(){
				// 获取达人推荐歌单
				uni.request({
					url: this.$apiBaseUrl.apiBaseUrl +"/top/playlist?limit=9&order=hot"
				}).then(res=>{
					res = res[1].data;
					// console.log(res);
					if(res.code==200){
						this.hostList = res.playlists;
					}
				})
				// 获取精品歌单
				uni.request({
					url: this.$apiBaseUrl.apiBaseUrl +"/top/playlist/highquality?limit=10"
				}).then(res=>{
					res = res[1].data;
					// console.log(res);
					if(res.code==200){
						this.highLevelList = res.playlists;
					}
				})
				// 获取官方推荐歌单
				uni.request({
					url: this.$apiBaseUrl.apiBaseUrl +"/personalized?limit=12"
				}).then(res=>{
					res = res[1].data;
					console.log(res);
					if(res.code==200){
						this.recommendLsit = res.result;
					}
				})
			}
			
		},
	}
</script>

<style>
	@import url("@/static/css/songList.css");
</style>
