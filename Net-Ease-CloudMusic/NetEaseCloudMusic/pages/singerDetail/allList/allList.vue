<template>
	<view class="allSong">
		<typeSongList :songDetailList="allSong"></typeSongList>
	</view>
</template>

<script>
	import typeSongList from "../../../components/typeSongList.vue";
	import {
		getAllSong
	} from "@/utils/request.js"
	export default {
		components: {
			typeSongList
		},
		data() {
			return {
				allSong: [],
				id:"",
			}
		},
		onLoad(e) {
			this.id = e.id;
			uni.setNavigationBarTitle({
				title: "全部演唱"
			})
			getAllSong(this.id,res=>{
				console.log(res);
				if (res.code == 200) {
					this.allSong = res.songs;
				}
			})
		},
		methods: {
			
		}
	}
</script>

<style>
.allSong{
	margin-top: -40rpx;
}
</style>
