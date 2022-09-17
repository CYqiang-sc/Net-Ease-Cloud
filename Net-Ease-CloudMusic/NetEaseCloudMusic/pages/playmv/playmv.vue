<template>
	<view class="videoPlayer">
		<video class="video" :src="videoSrc"></video>
		<view class="info" @click="toSong()">
			<view class="img">
				<image :src="picUrl" mode="" lazy-load></image>
			</view>
			<text class="song">{{song}}</text>
			<text class="singer">— {{singer}}</text>
		</view>
	</view>
</template>

<script>
	import {playmv} from "../../utils/request.js"
	export default {
		data() {
			return {
				mvid: '5436712',
				videoSrc: '',
				picUrl:'',
				song:'',
				singer:'',
				id:''
			};
		},
		onLoad(e) {
			console.log(e);
			this.mvid = e.mvid;
			this.picUrl = e.picUrl;
			this.song = e.song;
			this.singer = e.singer;
			this.id = e.songid;
			playmv(this.mvid,res=>{
				console.log(res);
				if(res.code==200){
					this.videoSrc = res.data.url;
				}
				console.log(this.videoSrc);
			})
		},
		methods:{
			toSong(){
				uni.navigateTo({
					url:'/pages/song/song?id='+this.id+'&name='+this.song
				})
			}
		}
	}
</script>

<style lang="scss">
@import url("@/static/css/mv.css");
</style>
