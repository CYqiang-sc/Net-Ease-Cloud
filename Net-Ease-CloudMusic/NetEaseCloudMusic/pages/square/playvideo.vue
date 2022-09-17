<template>
	<view class="videoPlayer">
		<video class="video" :src="videoSrc" :poster="coverurl"></video>
		<view class="info" @click="">
			<view class="img">
				<image :src="picUrl" mode="" lazy-load></image>
			</view>
			<text class="song">{{song}}</text>
			<text class="singer">— {{singer}}</text>
		</view>
	</view>
</template>

<script>
	import {playvideo} from "../../utils/request.js"
	export default {
		data() {
			return {
				videoid: '89ADDE33C0AAE8EC14B99F6750DB954D',
				videoSrc: 'http://vodkgeyttp9.vod.126.net/vodkgeyttp8/68sKFbGS_1328070069_hd.mp4?ts=1654661540&rid=3F17669D0F8D9322CB079618FE4044D9&rl=3&rs=SAxdrZfnezlokBBucdkjhnsclfUMnKBn&sign=85985fa59d4628a5d8c21e18232e052a&ext=HC8S90wPz4vfFtnzVb4RCJAEGoBfwcFlL94pEpqQRkHPcl4hY9rdPYyPQtWhRbOLfWh2hDZwgCoStjvLCRTI8zlQwHw12s10oaneFfxPdgmHhxBeOzhVKK2rIx4ELsfUWe90Kk%2Fv538Kkei2pdqGzzP3GjCD71jQL6wuOB26q864teyui%2FRIElEvdrQjbbfXoEWCcW9X%2FjW8m6iVpoGo0FunQbuNYBBMnAZc%2BYH1FCyZlLKE6WzLXQKT8vityHIs',
				coverurl:'',
				picUrl:'',
				song:'',
				singer:'',
				id:''
			};
		},
		onLoad(e) {
			this.videoid = e.videoid
			// console.log(this.videoid)
			this.coverurl = e.coverurl
			// console.log(this.picUrl)
			playvideo(this.videoid,res=>{
				// console.log(res,999);
			})
		},
		methods:{
			// toSong(){
			// 	uni.navigateTo({
			// 		url:'/pages/song/song?id='+this.id+'&name='+this.song
			// 	})
			// }
		}
	}
</script>

<style lang="scss">
@import url("@/static/css/mv.css");
</style>
