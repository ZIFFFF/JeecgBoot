<template>
  <PageWrapper>
    <template #headerContent> <WorkbenchHeader /> </template>
    <div class="lg:flex">
      
      <div class="lg:w-8/10 w-full !mr-4 enter-y"  v-if="hasPermission('duty:zs_key_duty:list')">
        <FullCalendar :loading="loading" class="enter-y" />
      </div>  
      <div class="lg:w-2/10 w-full enter-y">
        <QuickNav :loading="loading" />
        <DynamicInfo :loading="loading" class="!my-4 enter-y" />
      </div> 
    </div>
  </PageWrapper>
</template>
<script lang="ts" setup>
  import { ref } from 'vue';
  import { PageWrapper } from '/@/components/Page';
  import WorkbenchHeader from './components/WorkbenchHeader.vue';
  import FullCalendar from './components/FullCalendar.vue';
  import QuickNav from './components/QuickNav.vue';
  import DynamicInfo from './components/DynamicInfo.vue';
  import { usePermission } from '/@/hooks/web/usePermission';
  const { hasPermission } = usePermission();
  const loading = ref(true);

  setTimeout(() => {
    loading.value = false;
  }, 500);
</script>
