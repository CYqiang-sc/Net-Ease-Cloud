<template>
	<view class="content">
		<!-- 导航 -->
		<view class="navScroll">
			<!-- 地区 -->
			<scroll-view class="area" scroll-x>
				<view v-for="(item,index) in singerArea" :key="index" class="area-item"
					:class="{'active': item.selected}" @tap="changerArea(index,item)">
					{{item.name}}
				</view>
			</scroll-view>
			<!-- 类型 -->
			<scroll-view class="type" scroll-x>
				<view v-for="(item,index1) in singerType" :key="index1" class="type-item"
					@tap="changerType(index1,item)" :class="{'active': item.selected}">
					{{item.val}}
				</view>
			</scroll-view>
		</view>
		<!-- 歌手列表 -->
		<text class="hotsinger">热门歌手</text>
		<view class="singerList">
			<view class="singer-item" v-for="(item,index) in singerList" :key="index">
				<!-- <img src="" alt="" /> -->
				<view class="avatar"  @tap="toSinger(item)">
					<image :src="item.picUrl" alt="" lazy-load></image>
				</view>
				<text class="name"  @tap="toSinger(item)">{{item.name}}</text>


				<view class="follow" v-if="!isFollow[index]" @click="follow(item,index)">
					+ 关注
				</view>
				<view class="followed" v-else @click="cancel(item,index)">
					√ 已关注
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {

	} from "@/utils/request.js"
	export default {
		data() {
			return {
				singerArea: [{
						name: '全部',
						area: -1,
						selected: true
					},
					{
						name: '华语',
						area: 7,
						selected: false
					},
					{
						name: '欧美',
						area: 96,
						selected: false
					},
					{
						name: '日本',
						area: 8,
						selected: false
					},
					{
						name: '韩国',
						area: 16,
						selected: false
					},
					{
						name: '其他',
						area: 0,
						selected: false
					},
				],
				singerType: [{
						type: -1,
						val: '全部',
						selected: false
					},
					{
						type: 1,
						val: '男歌手',
						selected: false
					},
					{
						type: 2,
						val: '女歌手',
						selected: false
					},
					{
						type: 3,
						val: '乐队',
						selected: false
					}

				],
				type: "",
				area: "",
				initial: "",
				singerList: [],
				isFollow:[],//关注
				num: 99
			};
		},
		onShow(){
			this.getSingers();
		},
		methods: {
			// 获取歌手列表
			getSingers() {
				uni.request({
					url: this.$apiBaseUrl.apiBaseUrl + "/artist/list",
					data: {
						type: this.type,
						area: this.area,
						initial: this.initial
					}
				}).then((res) => {
					console.log(res[1].data);
					res = res[1].data;
					if (res.code == 200) {
						this.singerList = res.artists;
						this.singerList.forEach((item,index)=>{
							// this.$set(this.isFollow,index, item.followed) ;
							this.isFollow[index] = item.followed;
						})
					console.log('歌手列表',this.isFollow);
					}

				})
			},
			// 地区选择
			changerArea(index, item) {
				console.log("index: " + index);
				this.singerArea[index].selected = true;
				for (let i = 0; i < this.singerArea.length; i++) {
					if (i != index) {
						this.singerArea[i].selected = false
					}
				}
				this.area = item.area;
				this.getSingers();
			},
			// 类型选择
			changerType(index, item) {
				this.singerType[index].selected = true;
				for (let i = 0; i < this.singerType.length; i++) {
					if (i != index) {
						this.singerType[i].selected = false
					}
				}
				this.type = item.type;
				this.getSingers();
			},
			// 关注
			follow(item,index) {
				this.$set(this.isFollow,index, true) ;
				uni.request({
					url: this.$apiBaseUrl.apiBaseUrl + "/artist/sub",
					data: {
						id: item.id,
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
						
						
						// this.getSingers();
						return;
					}
				})
			},
			// 取消关注
			cancel(item,index) {
				this.$set(this.isFollow,index, false) ;
				uni.request({
					url: this.$apiBaseUrl.apiBaseUrl + "/artist/sub",
					data: {
						id: item.id,
						t: "0"
					}
				}).then((res) => {
					console.log(res);
					res = res[1].data;
					if (res.code == 200) {
						uni.showToast({
							title: "已取消关注",
							icon: "none",
							mask: true,
						});
						return;
					}
					// this.getSingers();
				})
			},
			// 去歌手歌单页面
			toSinger(item) {
				console.log(item.followed);
				uni.navigateTo({
					url: "/pages/singerDetail/singerDetail?id=" + item.id + "&followed="+item.followed
				})
			}

		},
		onLoad() {
			this.getSingers();
		}
	}
</script>

<style>
	@import url("@/static/css/singerList.css");
</style>
