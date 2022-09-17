<template>
	<view>
		<view class="text">
		<textarea class="font" name="" id="" cols="30" rows="10" v-model="sharetxt" placeholder="说说我的看法..."></textarea>
	</view>
	<button @tap="share()">发送</button>
	</view>
	
</template>

<script>
	import {shareAction} from "../../utils/request.js"
	export default {
		data(){
			return {
				sharetxt:"",
				threadId:""
			}
		},
		onLoad() { 
			this.threadId = uni.getStorageSync("threadId")
			console.log(this.threadId)
		},
		methods:{
			share(){
			let uid = uni.getStorageSync("uid")
				shareAction(uid,this.threadId,this.sharetxt,res =>{
					console.log(res)
				})
			}
		}
	}
</script>

<style>
.text{
	width: 96%;
	margin: 10rpx auto;
	border-radius: 2%;
	min-height: 400rpx;
	/* border: 1rpx gray solid; */
}
.font{
	font-size: 20px;
	color: dimgray;
	letter-spacing: 5px;
}
button{
	position: fixed;
	width: 100px;
	bottom: 10px;
	left: 140px;
	letter-spacing: 2px;
}
</style>