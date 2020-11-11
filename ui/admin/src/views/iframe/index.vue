<template>
  <div class="iframe-container">
    <iframe :src="key" scrolling="auto" frameborder="0" class="frame" />
  </div>
</template>

<script>
export default {
  name: 'Iframe',
  computed: {
    key() {
      return this.$route.name
    }
  }
}
</script>

<style lang="scss">
.iframe-container {
  position: absolute;
  top: 60px;
  left: 0px;
  right: 0px;
  bottom: 0px;
  padding-top: 50px;
  margin-top: -29px;
  .frame {
    position: relative;
    top: 0px;
    width: 100%;
    height: 100%;
  }
}
</style>
