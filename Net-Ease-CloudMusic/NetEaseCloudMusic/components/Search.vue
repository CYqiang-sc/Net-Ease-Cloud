<template>
  <view class="search">
    <input
      @click="inToSearch"
      class="search-item"
      type="text"
      placeholder="发现更多好音乐"
      disabled
    />
  </view>
</template>

<script>
export default {
  name: "Search",
  data() {
    return {};
  },
  methods: {
    inToSearch() {
		// 去search页面
      uni.navigateTo({ url: "/pages/search/search" });
    },
  },
};
</script>

<style scoped>
.search {
  background-color: rgb(255, 255, 255);
  display: flex;
}
.search-item {
  border: 1rpx solid #eee;
  flex: 1;
  margin: 20rpx 20rpx;
  height: 60rpx;
  font-size: 28rpx;
  text-align: center;
  line-height: 60rpx;
  color: #939594;
  background-color: #edefee;
  border-radius: 30rpx;
}
</style>