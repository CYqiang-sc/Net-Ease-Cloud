<template>
	<view class="container">
		<view class="wrapper">
			<view class="left-top-sign">注册</view>
			<view class="welcome">账号注册</view>
			<view class="input-content">
				<view class="input-item">
					<text class="tit">昵称</text>
					<input type="text" placeholder="请输入昵称" id="nikeName" v-model="nikeName" />
				</view>
				<view class="input-item">
					<text class="tit">手机号码</text>
					<input type="text" placeholder="请输入手机号码" id="phone" v-model="phone" />
				</view>
				<view class="input-item">
					<text class="tit">密码</text>
					<input type="password" placeholder="请输入密码" id="password" v-model="password" />
				</view>
				<view class="input-item">
					<text class="tit">确认密码</text>
					<input type="password" placeholder="请再次确认密码" id="password" v-model="password2" />
				</view>
				<view class="input-item-code">
					<view class="input-item-2">
						<text class="tit">验证码</text>
						<input type="text" placeholder="请输入验证码" v-model="passcode" />
					</view>
					<view class="code-btn">
						<button :disabled="codeTimer" :class="{ active: !coderTimer }" @click="codeBtn">
							{{ title }}
						</button>
					</view>
				</view>
			</view>

			<view>
				<button class="confirm-btn" @click="register">注册</button>
			</view>

			<view @click="login" class="change-login">
				已有账号？返回登录
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "Register",
		data() {
			return {
				isPass: false,
				title: "获取验证码",
				//注册信息
				nikeName: "",
				phone: "",
				password: "",
				password2: "",
				passcode: "",
				codeTimer: false,
				timer: 30,
			};
		},
		methods: {
			// 获取验证码事件
			codeBtn() {
				if (!this.phone) {
					uni.showToast({
						title: "手机号码不能为空",
						icon: "none",
						mask: true,
					});
					return;
				} else if (!/^1[3|4|5|7|8|9][0-9]{9}$/.test(this.phone)) {
					uni.showToast({
						title: "手机号码输入格式错误",
						icon: "none",
						mask: true,
					});
					return;
				}
				let timer = setInterval(() => {
					if (this.timer > 0) {
						this.timer--;
						this.codeTimer = true;
						this.title = `请${this.timer}s后重试`;
					} else {
						this.codeTimer = false;
						this.title = "获取验证码";
						this.timer = 30;
						clearInterval(timer);
					}
				}, 1000);
				// 发送获取验证码的请求
				uni.request({
					url: this.$apiBaseUrl.apiBaseUrl+"/captcha/sent",
					data: {
						phone: this.phone,
					},
				});
			},
			//注册
			register() {
				//字段校验
				if (this.nikeName == "") {
					uni.showToast({
						title: "昵称不能为空",
						icon: "none",
						mask: true,
					});
					return;
				} else if (this.phone == "") {
					uni.showToast({
						title: "手机号码不能为空",
						icon: "none",
						mask: true,
					});
					return;
				} else if (!this.password) {
					uni.showToast({
						title: "密码不能为空",
						icon: "none",
						mask: true,
					});
					return;
				} else if (!/^[0-9a-zA-Z_]{1,}$/.test(this.nikeName)) {
					uni.showToast({
						title: "昵称只能由数字、字母、下划线组成",
						icon: "none",
						mask: true,
					});
					return;
				} else if (!/^1[3|4|5|7|8|9][0-9]{9}$/.test(this.phone)) {
					uni.showToast({
						title: "手机号码输入格式错误",
						icon: "none",
						mask: true,
					});
					return;
				} //((?=.*\d)(?=.*\D)|(?=.*[a-zA-Z])(?=.*[^a-zA-Z]))(?!^.*[\u4E00-\u9FA5].*$)/
				else if (!/((?=.*\d)(?=.*\D)|(?=.*[a-zA-Z])(?=.*[^a-zA-Z]))(?!^.*[\u4E00-\u9FA5].*$)/.test(this
						.password)) {
					uni.showToast({
						title: "密码需至少包含数字、字母、符号中的2种",
						icon: "none",
						mask: true,
					});
					return;
				} else if (this.password != this.password2) {
					uni.showToast({
						title: "两次密码输入不一致，请检查",
						icon: "none",
						mask: true,
					});
				} else if (!this.passcode) {
					uni.showToast({
						title: "验证码不能为空",
						icon: "none",
						mask: true,
					});
					return;
				} else {
					// 验证码是否正确
					uni.request({
						url: this.$apiBaseUrl.apiBaseUrl+"/captcha/verify",
						data: {
							phone: this.phone,
							captcha: this.passcode,
						},
					}).then((res) => {
						console.log(res);
						var res = res[1].data
						if (res.code === 200) {
							//注册接口
							uni.request({
								url: this.$apiBaseUrl.apiBaseUrl+"/register/cellphone",
								data: {
									captcha: this.passcode,
									phone: this.phone,
									password: this.password,
									nickname: this.nikeName
								},
							}).then((res) => {
								console.log(res);
								//注册成功
							})
						}
					})
				}

			},


			//返回登录  
			login() {
				uni.navigateTo({
					url: "/pages/login/login"
				})

			}
		},
	};
</script>

<style scoped>
	/* pages/login/login.wxss */
	.wrapper {
		position: relative;
		z-index: 90;
		padding-bottom: 40rpx;
	}

	.left-top-sign {
		font-size: 120rpx;
		color: #f8f8f8;
		position: relative;
		left: -16rpx;
	}

	.welcome {
		position: relative;
		left: 50rpx;
		top: -90rpx;
		font-size: 46rpx;
		color: #d43c33;
	}

	.input-content {
		padding: 0 60rpx;
	}

	.input-item {
		display: flex;
		flex-direction: column;
		align-items: flex-start;
		justify-content: center;
		padding: 0 30rpx;
		background: #f8f6fc;
		height: 120rpx;
		border-radius: 10px;
		margin-bottom: 50rpx;
	}

	.input-item-code {
		display: flex;
		flex-direction: row;
	}

	.input-item-2 {
		display: flex;
		flex-direction: column;
		align-items: flex-start;
		justify-content: center;
		padding: 0 30rpx;
		background: #f8f6fc;
		height: 120rpx;
		border-radius: 10px;
	}

	.input-item-code .code-btn button {
		height: 120rpx;
		font-size: 30rpx;
		line-height: 120rpx;
		border-radius: 10rpx;
	}

	.input-item:last-child {
		margin-bottom: 0;
	}

	.tit {
		height: 50rpx;
		line-height: 56rpx;
		font-size: 30rpx;
		color: #606266;
	}

	.input-item input {
		height: 60rpx;
		font-size: 30rpx;
		color: #303133;
		width: 100%;
	}

	.change-login {
		font-size: 30rpx;
		color: rgb(0, 191, 255);
		margin-left: 60rpx;
		margin-top: 20rpx;
	}

	.confirm-btn {
		width: 630rpx !important;
		height: 76rpx;
		line-height: 76rpx;
		border-radius: 50rpx;
		margin-top: 70rpx;
		background: #d43c33;
		color: #fff;
		font-size: 32rpx;
		padding: 0;
	}

	.confirm-btn2:after {
		border-radius: 100px;
	}

	.forget-section {
		font-size: 28rpx;
		color: rgb(0, 191, 255);
		text-align: center;
		margin-top: 40rpx;
	}

	.register-section {
		position: absolute;
		left: 0;
		bottom: 50rpx;
		width: 100%;
		font-size: 28rpx;
		color: #606266;
		text-align: center;
	}

	.register-section text {
		color: rgb(0, 191, 255);
		margin-left: 10rpx;
	}

	.active {
		color: rgb(255, 25, 25);
	}
</style>
